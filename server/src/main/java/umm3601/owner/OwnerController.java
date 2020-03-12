package umm3601.owner;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mongojack.JacksonCodecRegistry;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

/**
 * Controller that manages requests for owner data.
 */
public class OwnerController {

  static String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

  JacksonCodecRegistry jacksonCodecRegistry = JacksonCodecRegistry.withDefaultObjectMapper();

  private final MongoCollection<Owner> ownerCollection;

  /**
   * @param database the database containing owner data
   */
  public OwnerController(MongoDatabase database) {
    jacksonCodecRegistry.addCodecForClass(Owner.class);
    ownerCollection = database.getCollection("owners").withDocumentClass(Owner.class)
        .withCodecRegistry(jacksonCodecRegistry);
  }

  /**
   * Get the owner specified by the `id` parameter in the request.
   *
   * @param ctx a Javalin HTTP context
   */
  public void getOwner(Context ctx) {
    String id = ctx.pathParam("id");
    Owner owner;
    try {
      owner = ownerCollection.find(eq("_id", new ObjectId(id))).first();
    } catch(IllegalArgumentException e) {
      throw new BadRequestResponse("The requested owner id wasn't a legal Mongo Object ID.");
    }
    if (owner == null) {
      throw new NotFoundResponse("The requested owner was not found.");
    } else {
      ctx.json(owner);
    }
  }

  /**
   * Delete the owner specified by the `id` parameter in the request.
   *
   * @param ctx a Javalin HTTP context
   */
  public void deleteOwner(Context ctx) {
    String id = ctx.pathParam("id");
    ownerCollection.deleteOne(eq("_id", new ObjectId(id)));
  }

  /**
   * Get a sorted JSON response in ascending order that filters by the query parameters
   * supplied by the Javalin HTTP context
   *
   * @param ctx a Javalin HTTP context
   */
  public void getOwners(Context ctx) {

    List<Bson> filters = new ArrayList<Bson>(); // start with a blank JSON document

    if (ctx.queryParamMap().containsKey("name")) {
      filters.add(regex("name", ctx.queryParam("name"), "i"));
    }

    if (ctx.queryParamMap().containsKey("email")) {
      filters.add(regex("email", ctx.queryParam("email"), "i"));
    }

    if (ctx.queryParamMap().containsKey("building")) {
      filters.add(regex("building", ctx.queryParam("building"), "i"));
    }

    if (ctx.queryParamMap().containsKey("officenumber")) {
      filters.add(regex("officenumber", ctx.queryParam("officenumber"), "i"));
    }

    String sortBy = ctx.queryParam("sortby", "name"); // Sort by query param, default is name
    String sortOrder = ctx.queryParam("sortorder", "asc"); // Sort order query param, default is ascending

    ctx.json(ownerCollection.find(filters.isEmpty() ? new Document() : and(filters))
      .sort(sortOrder.equals("desc") ? Sorts.descending(sortBy) : Sorts.ascending(sortBy))
      .into(new ArrayList<>()));
  }

  /**
   * Get a Javalin response that confirms we added a new owner with the specified parameters
   *
   * @param ctx a Javalin HTTP context (holds our request parameters)
   */
  public void addNewOwner(Context ctx) {
    Owner newOwner = ctx.bodyValidator(Owner.class)
      .check((owner) -> owner.name != null && owner.name.length() > 0) // Make sure the `name` field was supplied and non-empty
      .check((owner) -> owner.building != null && owner.building.length() > 0)  // Make sure the `building` field was supplied and non-empty
      .check((owner) -> owner.officeNumber != null && owner.officeNumber.length() > 0)  // Make sure the `officeNumber` field was supplied and non-empty
      .check((owner) -> owner.email.matches(emailRegex))  // Make sure the `email` field was supplied and valid
      .get();

    ownerCollection.insertOne(newOwner);
    ctx.status(201);
    ctx.json(ImmutableMap.of("id", newOwner._id));
  }
}
