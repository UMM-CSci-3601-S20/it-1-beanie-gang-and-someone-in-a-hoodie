package umm3601.note;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;

/**
 * Controller that manages requests for note data (for a specific owner).
 */
public class NoteController {

  JacksonCodecRegistry jacksonCodecRegistry = JacksonCodecRegistry.withDefaultObjectMapper();

  private final MongoCollection<Note> noteCollection;

  /**
   * @param database the database containing the note data
   */
  public NoteController(MongoDatabase database) {
    jacksonCodecRegistry.addCodecForClass(Note.class);
    noteCollection = database.getCollection("notes").withDocumentClass(Note.class)
        .withCodecRegistry(jacksonCodecRegistry);
  }

  /**
   * Get a note belonging to a specific owner.
   * Uses the following parameters in the request:
   *
   * `id` parameter -> note id
   * `ownerid` -> which owner's notes
   *
   * @param ctx a Javalin HTTP context
   */
  public void getNoteById(Context ctx) {
    String id = ctx.pathParam("id");
    String ownerID = ctx.queryParam("ownerid");
    Note note;

    try {
      note = noteCollection.find(eq("_id", new ObjectId(id))).first();
    } catch(IllegalArgumentException e) {
      throw new BadRequestResponse("The requested note id wasn't a legal Mongo Object ID.");
    }
    if (note == null) {
      throw new NotFoundResponse("The requested note was not found.");
    } else if (note.ownerID != ownerID) {
      throw new NotFoundResponse("The requested note does not belong to this owner.");
    } else {
      ctx.json(note);
    }
  }

  /**
   * Delete a note belonging to a specific owner.
   * Uses the following parameters in the request:
   *
   * `id` parameter -> note id
   * `ownerid` -> which owner's notes
   *
   * @param ctx a Javalin HTTP context
   */
  public void deleteNote(Context ctx) {
    String id = ctx.pathParam("id");
    String ownerID = ctx.queryParam("ownerid");
    Note note;

    try {
      note = noteCollection.find(eq("_id", new ObjectId(id))).first();
    } catch(IllegalArgumentException e) {
      throw new BadRequestResponse("The requested note id wasn't a legal Mongo Object ID.");
    }
    if (note == null) {
      throw new NotFoundResponse("The requested does not exist.");
    } else if (note.ownerID != ownerID) {
      throw new ForbiddenResponse("The requested note does not belong to this owner. It cannot be deleted.");
    } else {
      noteCollection.deleteOne(eq("_id", new ObjectId(id)));
    }
  }

  /**
   * Get a sorted JSON response in ascending order that filters by the query parameters
   * supplied by the Javalin HTTP context
   *
   * @param ctx a Javalin HTTP context
   */
  public void getNotesByOwner(Context ctx) {
    List<Bson> filters = new ArrayList<Bson>(); // start with a blank JSON document
    if (ctx.queryParamMap().containsKey("ownerid")) {
      System.out.println("QueryParam map contains ownerid" );
      String targetOwnerID = ctx.queryParam("ownerid");
      System.out.println(targetOwnerID);
      filters.add(eq("ownerID", targetOwnerID));
    }
    if (ctx.queryParamMap().containsKey("body")) {
      filters.add(regex("body", ctx.queryParam("body"), "i"));
    }
    if (ctx.queryParamMap().containsKey("status")) {
      filters.add(eq("status", ctx.queryParam("status")));
    }

    String sortBy = ctx.queryParam("sortBy", "status"); //Sort by query param, default being `status`
    String sortOrder = ctx.queryParam("sortorder", "asc");

    ctx.json(noteCollection.find(filters.isEmpty() ? new Document() : and(filters))
      .sort(sortOrder.equals("desc") ?  Sorts.descending(sortBy) : Sorts.ascending(sortBy))
      .into(new ArrayList<>()));
  }

  /**
   * Add a new note and confirm with a successful JSON response
   *
   * @param ctx a Javalin HTTP context
   */
  public void addNewNote(Context ctx) {
    Note newNote = ctx.bodyValidator(Note.class)
      .check((note) -> note.ownerID != null && note.ownerID.length() == 24) // 24 character hex ID
      .check((note) -> note.body != null && note.body.length() > 0) // Make sure the body is not empty
      .check((note) -> note.addDate != null && note.addDate.matches("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d([+, -])\\d\\d\\d\\d")) // Regex to match an ISO 8601 time string
      .check((note) -> note.expireDate != null && note.expireDate.matches("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d([+, -])\\d\\d\\d\\d")) // Regex to match an ISO 8601 time string
      .check((note) -> note.status.matches("^(active|draft|deleted|template)$")) // Status should be one of these
      .get();

      noteCollection.insertOne(newNote);
      ctx.status(201);
      ctx.json(ImmutableMap.of("id", newNote._id));
  }

  /**
   * Edit an existing note
   */
  public void editNote(Context ctx) {

    Document inputDoc = ctx.bodyAsClass(Document.class); //throws 400 error

    String id = ctx.pathParam("id");
    Note note;

    try {
      note = noteCollection.find(eq("_id", new ObjectId(id))).first();
    } catch(IllegalArgumentException e) {
      throw new BadRequestResponse("The requested note id wasn't a legal Mongo Object ID.");
    }
    if (note == null) {
      throw new NotFoundResponse("The requested note does not exist.");
    } else {
      HashSet<String> validKeys = new HashSet<String>(Arrays.asList("body", "expireDate", "status"));
      HashSet<String> forbiddenKeys = new HashSet<String>(Arrays.asList("ownerID", "addDate", "_id"));


      noteCollection.updateOne(eq("_id", id), null);
      ctx.status(200);
      ctx.json(ImmutableMap.of("id", id));
    }


  }
}
