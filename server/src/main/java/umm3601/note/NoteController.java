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
import java.util.Timer;

import com.google.common.collect.ImmutableMap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mongojack.JacksonCodecRegistry;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;
import umm3601.UnprocessableResponse;

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
      .check((note) -> note.expireDate == null || note.expireDate.matches("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d([+, -])\\d\\d\\d\\d")) // Regex to match an ISO 8601 time string
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
    Document toEdit = new Document();
    Document toReturn = new Document();

    String id = ctx.pathParam("id");
    Note note;

    if(inputDoc.isEmpty()) {
      throw new BadRequestResponse("PATCH request must contain a body.");
    }

    try {
      note = noteCollection.find(eq("_id", new ObjectId(id))).first();
      // This really isn't the right way to do things.  Retrieving the database object
      // in order to check if it exists is inefficient.  We will need to do this at some
      // point, in order to enfore non-active notices not gaining expiration dates--but
      // we can probably move that later.  It's a question of: do the expensive thing always;
      // or do the cheap thing always, and sometimes the expensive thing as well.
    } catch(IllegalArgumentException e) {
      throw new BadRequestResponse("The requested note id wasn't a legal Mongo Object ID.");
    }
    if (note == null) {
      throw new NotFoundResponse("The requested note does not exist.");
    } else {
      HashSet<String> validKeys = new HashSet<String>(Arrays.asList("body", "expireDate", "status"));
      HashSet<String> forbiddenKeys = new HashSet<String>(Arrays.asList("ownerID", "addDate", "_id"));
      HashSet<String> validStatuses = new HashSet<String>(Arrays.asList("active", "draft", "deleted", "template"));
      for (String key: inputDoc.keySet()) {
        if(forbiddenKeys.contains(key)) {
          throw new BadRequestResponse("Cannot edit the field " + key + ": this field is not editable and should be considered static.");
        } else if (!(validKeys.contains(key))){
          throw new ConflictResponse("Cannot edit the nonexistant field " + key + ".");
        }
      }

        // At this point, we're taking information from the user and putting it directly into the database.
        // I'm unsure of how to properly sanitize this; StackOverflow just says to use PreparedStatements instead
        // of Statements, but thanks to the magic of mongodb I'm not using either.  At this point I'm going to cross
        // my fingers really hard and pray that this will be fine.

        if(inputDoc.containsKey("body")) {
          toEdit.append("body", inputDoc.get("body"));
        }
        if(inputDoc.containsKey("expireDate")){
          if(inputDoc.get("expireDate") == null) {
            toReturn.append("$unset", new Document("expireDate", "")); //If expireDate is specifically included with a null value, remove the expiration date.
          } else if(inputDoc.get("expireDate").toString() //This assumes that we're using the same string encoding they are, but it's our own API we should be fine.
          .matches("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d([+, -])\\d\\d\\d\\d")) {
            toEdit.append("expireDate", inputDoc.get("expireDate"));
          } else {
            throw new UnprocessableResponse("The 'expireDate' field must contain an ISO 8061 time string.");
          }

        }
        if(inputDoc.containsKey("status")) {
          if(validStatuses.contains(inputDoc.get("status"))){
            toEdit.append("status", inputDoc.get("status"));
          } else {
            throw new UnprocessableResponse("The 'status' field must contain one of 'active', 'draft', 'deleted', or 'template'.");
          }
        }

      }


      noteCollection.updateOne(eq("_id", new ObjectId(id)), new Document("$set", toEdit));
      ctx.status(204);
    }

  }


