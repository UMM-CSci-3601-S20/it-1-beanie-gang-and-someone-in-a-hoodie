package umm3601.user;

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
 * Controller that manages requests for info about users.
 */
public class UserController {

  static String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

  JacksonCodecRegistry jacksonCodecRegistry = JacksonCodecRegistry.withDefaultObjectMapper();

  private final MongoCollection<User> userCollection;

  /**
   * Construct a controller for users.
   *
   * @param database the database containing user data
   */
  public UserController(MongoDatabase database) {
    jacksonCodecRegistry.addCodecForClass(User.class);
    userCollection = database.getCollection("users").withDocumentClass(User.class)
        .withCodecRegistry(jacksonCodecRegistry);
  }

  /**
   * Get the single user specified by the `id` parameter in the request.
   *
   * @param ctx a Javalin HTTP context
   */
  public void getUser(Context ctx) {
    String id = ctx.pathParam("id");
    User user;

    try {
      user = userCollection.find(eq("_id", new ObjectId(id))).first();
    } catch(IllegalArgumentException e) {
      throw new BadRequestResponse("The requested user id wasn't a legal Mongo Object ID.");
    }
    if (user == null) {
      throw new NotFoundResponse("The requested user was not found");
    } else {
      ctx.json(user);
    }
  }

  /**
   * Delete the user specified by the `id` parameter in the request.
   *
   * @param ctx a Javalin HTTP context
   */
  public void deleteUser(Context ctx) {
    String id = ctx.pathParam("id");
    userCollection.deleteOne(eq("_id", new ObjectId(id)));
  }

  /**
   * Get a JSON response with a list of all the users.
   *
   * @param ctx a Javalin HTTP context
   */
  public void getUsers(Context ctx) {

    List<Bson> filters = new ArrayList<Bson>(); // start with a blank document

    if (ctx.queryParamMap().containsKey("age")) {
        int targetAge = ctx.queryParam("age", Integer.class).get();
        filters.add(eq("age", targetAge));
    }

    if (ctx.queryParamMap().containsKey("company")) {
      filters.add(regex("company", ctx.queryParam("company"), "i"));
    }

    if (ctx.queryParamMap().containsKey("role")) {
      filters.add(eq("role", ctx.queryParam("role")));
    }

    String sortBy = ctx.queryParam("sortby", "name"); //Sort by sort query param, default is name
    String sortOrder = ctx.queryParam("sortorder", "asc");

    ctx.json(userCollection.find(filters.isEmpty() ? new Document() : and(filters))
      .sort(sortOrder.equals("desc") ?  Sorts.descending(sortBy) : Sorts.ascending(sortBy))
      .into(new ArrayList<>()));
  }

  /**
   * Get a JSON response with a list of all the users.
   *
   * @param ctx a Javalin HTTP context
   */
  public void addNewUser(Context ctx) {
    User newUser = ctx.bodyValidator(User.class)
      .check((usr) -> usr.name != null && usr.name.length() > 0) //Verify that the user has a name that is not blank
      .check((usr) -> usr.email.matches(emailRegex)) // Verify that the provided email is a valid email
      .check((usr) -> usr.age > 0) // Verify that the provided age is > 0
      .check((usr) -> usr.role.matches("^(admin|editor|viewer)$")) // Verify that the role is one of the valid roles
      .check((usr) -> usr.company != null && usr.company.length() > 0) // Verify that the user has a company that is not blank
      .get();

    // Generate user avatar (you won't need this part for todos)
    try {
      newUser.avatar = "https://gravatar.com/avatar/" + md5(newUser.email) + "?d=identicon";  // generate unique md5 code for identicon
    } catch (NoSuchAlgorithmException ignored) {
      newUser.avatar = "https://gravatar.com/avatar/?d=mp";                           // set to mystery person
    }

    userCollection.insertOne(newUser);
    ctx.status(201);
    ctx.json(ImmutableMap.of("id", newUser._id));
  }

  /**
   * Utility function to generate the md5 hash for a given string
   *
   * @param str the string to generate a md5 for
   */
  public String md5(String str) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] hashInBytes = md.digest(str.toLowerCase().getBytes(StandardCharsets.UTF_8));

    String result = "";
    for (byte b : hashInBytes) {
      result += String.format("%02x", b);
    }
    return result;
  }
}
