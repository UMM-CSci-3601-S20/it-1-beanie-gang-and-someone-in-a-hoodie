package umm3601.note;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.s;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.util.ContextUtil;
import io.javalin.plugin.json.JavalinJson;
import umm3601.UnprocessableResponse;

public class NoteControllerSpec {

  MockHttpServletRequest mockReq = new MockHttpServletRequest();
  MockHttpServletResponse mockRes = new MockHttpServletResponse();

  private NoteController noteController;

  private ObjectId samsNoteId;

  static MongoClient mongoClient;
  static MongoDatabase db;

  static ObjectMapper jsonMapper = new ObjectMapper();

  @BeforeAll
  public static void setupAll() {
    String mongoAddr = System.getenv().getOrDefault("MONGO_ADDR", "localhost");
    mongoClient = MongoClients.create(MongoClientSettings.builder()
        .applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress(mongoAddr)))).build());

    db = mongoClient.getDatabase("test");
  }

  @BeforeEach
  public void setupEach() throws IOException {
    // Reset our mock request and response objects
    mockReq.resetAll();
    mockRes.resetAll();

    MongoCollection<Document> noteDocuments = db.getCollection("notes");
    noteDocuments.drop();
    List<Document> testNotes = new ArrayList<>();
    testNotes.add(Document
        .parse("{ " + "ownerID: \"owner1_ID\", " + "body: \"I am running 5 minutes late to my non-existent office\", "
            + "addDate: \"2020-03-07T22:03:38+0000\", " + "expireDate: \"2021-03-20T22:03:38+0000\", "
            + "status: \"active\"" + "}"));
    testNotes.add(Document.parse("{ " + "ownerID: \"owner1_ID\", " + "body: \"I am never coming to my office again\", "
        + "addDate: \"2020-03-07T22:03:38+0000\", " + "expireDate: \"2099-03-07T22:03:38+0000\", "
        + "status: \"active\"" + "}"));
    testNotes.add(Document.parse("{ " + "ownerID: \"owner2_ID\", " + "body: \"I am on sabbatical no office hours\", "
        + "addDate: \"2020-03-07T22:03:38+0000\", " + "expireDate: \"2021-03-07T22:03:38+0000\", "
        + "status: \"active\"" + "}"));
    testNotes.add(Document.parse("{ " + "ownerID: \"owner2_ID\", " + "body: \"Go to owner3's office\", "
        + "addDate: \"2020-03-07T22:03:38+0000\", " + "expireDate: \"2020-03-21T22:03:38+0000\", "
        + "status: \"active\"" + "}"));
    testNotes.add(Document.parse("{ " + "ownerID: \"owner3_ID\", "
        + "body: \"Not many come to my office I offer donuts\", " + "addDate: \"2020-03-07T22:03:38+0000\", "
        + "expireDate: \"2021-03-07T22:03:38+0000\", " + "status: \"active\"" + "}"));
    samsNoteId = new ObjectId();
    BasicDBObject sam = new BasicDBObject("_id", samsNoteId);
    sam = sam.append("ownerID", "owner3_ID").append("body", "I am sam").append("addDate", "2020-03-07T22:03:38+0000")
        .append("expireDate", "2100-03-07T22:03:38+0000").append("status", "active");

    noteDocuments.insertMany(testNotes);
    noteDocuments.insertOne(Document.parse(sam.toJson()));

    noteController = new NoteController(db);
  }

  @AfterAll
  public static void teardown() {
    db.drop();
    mongoClient.close();
  }

  @Test
  public void getAllNotesForOwner1() {
    mockReq.setQueryString("ownerid=owner1_ID");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes");

    noteController.getNotesByOwner(ctx);

    assertEquals(200, mockRes.getStatus());

    String result = ctx.resultString();
    Note[] resultNotes = JavalinJson.fromJson(result, Note[].class);

    assertEquals(2, resultNotes.length);
    for (Note note : resultNotes) {
      assertEquals("owner1_ID", note.ownerID, "Incorrect ID");
    }
  }

  @Test
  public void addNote() throws IOException {
    String testNewNote = "{ " + "\"ownerID\": \"e7fd674c72b76596c75d9f1e\", " + "\"body\": \"Test Body\", "
        + "\"addDate\": \"2020-03-07T22:03:38+0000\", " + "\"expireDate\": \"2021-03-07T22:03:38+0000\", "
        + "\"status\": \"active\" }";

    mockReq.setBodyContent(testNewNote);
    mockReq.setMethod("POST");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/new");

    noteController.addNewNote(ctx);

    assertEquals(201, mockRes.getStatus());

    String result = ctx.resultString();
    String id = jsonMapper.readValue(result, ObjectNode.class).get("id").asText();
    assertNotEquals("", id);
    System.out.println(id);

    assertEquals(1, db.getCollection("notes").countDocuments(eq("_id", new ObjectId(id))));

    Document addedNote = db.getCollection("notes").find(eq("_id", new ObjectId(id))).first();
    assertNotNull(addedNote);
    assertEquals("e7fd674c72b76596c75d9f1e", addedNote.getString("ownerID"));
    assertEquals("Test Body", addedNote.getString("body"));
    assertEquals("2020-03-07T22:03:38+0000", addedNote.getString("addDate"));
    assertEquals("2021-03-07T22:03:38+0000", addedNote.getString("expireDate"));
    assertEquals("active", addedNote.getString("status"));
  }

  @Test
  public void editSingleField() throws IOException {
    String reqBody = "{\"body\": \"I am not sam anymore\"}";
    mockReq.setBodyContent(reqBody);
    mockReq.setMethod("PATCH");
    // Because we're partially altering an object, we make a body with just the
    // alteration and use the PATCH (not PUT) method

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/:id", ImmutableMap.of("id", samsNoteId.toHexString()));
    noteController.editNote(ctx);

    assertEquals(204, mockRes.getStatus());
    // We don't have a good way to return just the edited object right now,
    // so we return nothing in the body and show that with a 204 response.

    assertEquals(1, db.getCollection("notes").countDocuments(eq("_id", samsNoteId)));
    // There should still be exactly one note per id, and the id shouldn't have
    // changed.

    Document editedNote = db.getCollection("notes").find(eq("_id", samsNoteId)).first();
    assertNotNull(editedNote);
    // The note should still actually exist

    assertEquals("I am not sam anymore", editedNote.getString("body"));
    // The edited field should show the new value

    assertEquals("owner3_ID", editedNote.getString("ownerID"));
    assertEquals("active", editedNote.getString("status"));
    assertEquals("2020-03-07T22:03:38+0000", editedNote.getString("addDate"));
    assertEquals("2100-03-07T22:03:38+0000", editedNote.getString("expireDate"));
    // all other fields should be untouched
  }

  @Test
  public void editMultipleFields() throws IOException {
    String reqBody = "{\"body\": \"I am still sam\", \"expireDate\": \"2025-03-07T22:03:38+0000\"}";
    mockReq.setBodyContent(reqBody);
    mockReq.setMethod("PATCH");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/:id", ImmutableMap.of("id", samsNoteId.toHexString()));
    noteController.editNote(ctx);

    assertEquals(204, mockRes.getStatus());

    assertEquals(1, db.getCollection("notes").countDocuments(eq("_id", samsNoteId)));

    Document editedNote = db.getCollection("notes").find(eq("_id", samsNoteId)).first();
    assertNotNull(editedNote);

    assertEquals("I am still sam", editedNote.getString("body"));
    assertEquals("2025-03-07T22:03:38+0000", editedNote.getString("expireDate"));

    assertEquals("active", editedNote.getString("status"));
    assertEquals("owner3_ID", editedNote.getString("ownerID"));
    assertEquals("2020-03-07T22:03:38+0000", editedNote.getString("addDate"));
  }

  @Test
  public void editMissingId() throws IOException {
    String reqBody = "{\"body\": \"I am not sam anymore\"}";
    mockReq.setBodyContent(reqBody);
    mockReq.setMethod("PATCH");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/:id",
        ImmutableMap.of("id", "58af3a600343927e48e87335"));

    assertThrows(NotFoundResponse.class, () -> {
      noteController.editNote(ctx);
    });
  }

  @Test
  public void editBadId() throws IOException {
    String reqBody = "{\"body\": \"I am not sam anymore\"}";
    mockReq.setBodyContent(reqBody);
    mockReq.setMethod("PATCH");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/:id",
        ImmutableMap.of("id", "this garbage isn't an id!"));

    assertThrows(BadRequestResponse.class, () -> {
      noteController.editNote(ctx);
    });
  }

  @Test
  public void editIdWithMalformedBody() throws IOException {
    mockReq.setBodyContent("This isn't parsable as a document");
    mockReq.setMethod("PATCH");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/:id", ImmutableMap.of("id", samsNoteId.toHexString()));

    assertThrows(BadRequestResponse.class, () -> {
      noteController.editNote(ctx);
    });
  }

  @Test
  public void editIdWithInvalidValue() throws IOException {
    mockReq.setBodyContent("{\"expireDate\": \"not actually a date\"}");
    mockReq.setMethod("PATCH");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/:id", ImmutableMap.of("id", samsNoteId.toHexString()));

    assertThrows(UnprocessableResponse.class, () -> {
      noteController.editNote(ctx);
    });
    // HTTP 422 Unprocessable Entity: the entity could be syntactically parsed but
    // was semantically garbage.
    // In this case, it's because a non-date-string was attempted to be inserted
    // into a location that requires
    // a date string.
  }

  @Test
  public void editIdWithBadKeys() throws IOException {
    mockReq.setBodyContent("{\"badKey\": \"irrelevant value\"}");
    mockReq.setMethod("PATCH");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/:id", ImmutableMap.of("id", samsNoteId.toHexString()));

    assertThrows(ConflictResponse.class, () -> {
      noteController.editNote(ctx);
    });
    // ConflictResponse represents a 409 error, in this case an attempt to edit a
    // nonexistent field.
  }

  @Test
  public void editIdWithIllegalKeys() throws IOException {
    mockReq.setBodyContent("{\"ownerID\": \"Charlie\"}");
    mockReq.setMethod("PATCH");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/:id", ImmutableMap.of("id", samsNoteId.toHexString()));

    assertThrows(BadRequestResponse.class, () -> {
      noteController.editNote(ctx);
    });
  }

  // The 422 and 409 errors could be switched between these conditions, or they
  // could possibly both be 409?
  // Additionally, should attempting to edit a non-editable field (id, ownerID, or
  // addDate) throw a 422, 409, 400, or 403?

  @Test
  public void AddNoteWithoutExpiration() throws IOException {
    String testNewNote = "{ " + "\"ownerID\": \"e7fd674c72b76596c75d9f1e\", " + "\"body\": \"Test Body\", "
        + "\"addDate\": \"2020-03-07T22:03:38+0000\", " + "\"status\": \"active\" }";

    mockReq.setBodyContent(testNewNote);
    mockReq.setMethod("POST");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/new");

    noteController.addNewNote(ctx);

    assertEquals(201, mockRes.getStatus());

    String result = ctx.resultString();
    String id = jsonMapper.readValue(result, ObjectNode.class).get("id").asText();
    assertNotEquals("", id);
    System.out.println(id);

    assertEquals(1, db.getCollection("notes").countDocuments(eq("_id", new ObjectId(id))));

    Document addedNote = db.getCollection("notes").find(eq("_id", new ObjectId(id))).first();
    assertNotNull(addedNote);
    assertEquals("e7fd674c72b76596c75d9f1e", addedNote.getString("ownerID"));
    assertEquals("Test Body", addedNote.getString("body"));
    assertEquals("2020-03-07T22:03:38+0000", addedNote.getString("addDate"));
    assertNull(addedNote.getString("expireDate"));
    assertEquals("active", addedNote.getString("status"));
  }

  @Test
  public void RemoveExpirationFromNote() throws IOException {
    mockReq.setBodyContent("{\"expireDate\", \"%00\"}");
    mockReq.setMethod("PATCH");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/:id", ImmutableMap.of("id", samsNoteId.toHexString()));

    noteController.editNote(ctx);

    assertEquals(204, mockRes.getStatus());

    assertEquals(1, db.getCollection("notes").countDocuments(eq("_id", samsNoteId)));

    Document editedNote = db.getCollection("notes").find(eq("_id", samsNoteId)).first();
    assertNotNull(editedNote);

    assertNull(editedNote.getString("expireDate"));

    assertEquals("active", editedNote.getString("status"));
    assertEquals("I am sam", editedNote.getString("body"));
    assertEquals("owner3_ID", editedNote.getString("ownerID"));
    assertEquals("2020-03-07T22:03:38+0000", editedNote.getString("addDate"));
  }

  @Test
  public void AddExpirationToNote() throws IOException {
    // This is... a little ugly. And relies on something else working. But there
    // isn't a great way of knowing
    // the ID of another notice without an expiration date.

    String testNewNote = "{ " + "\"ownerID\": \"e7fd674c72b76596c75d9f1e\", " + "\"body\": \"Test Body\", "
        + "\"addDate\": \"2020-03-07T22:03:38+0000\", " + "\"status\": \"active\" }";

    mockReq.setBodyContent(testNewNote);
    mockReq.setMethod("POST");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/new");

    noteController.addNewNote(ctx);

    String id = jsonMapper.readValue(ctx.resultString(), ObjectNode.class).get("id").asText();
    mockRes.resetAll();

    mockReq.setBodyContent("{\"expireDate\": \"2021-03-07T22:03:38+0000\"}");
    mockReq.setMethod("PATCH");
    ctx = ContextUtil.init(mockReq, mockRes, "api/notes/:id", ImmutableMap.of("id", new ObjectId(id).toHexString()));
    noteController.editNote(ctx);

    assertEquals(1, db.getCollection("notes").countDocuments(eq("_id", new ObjectId(id))));

    Document addedNote = db.getCollection("notes").find(eq("_id", new ObjectId(id))).first();
    assertNotNull(addedNote);
    assertEquals("e7fd674c72b76596c75d9f1e", addedNote.getString("ownerID"));
    assertEquals("Test Body", addedNote.getString("body"));
    assertEquals("2020-03-07T22:03:38+0000", addedNote.getString("addDate"));
    assertEquals("2021-03-07T22:03:38+0000", addedNote.getString("expireDate"));
    assertEquals("active", addedNote.getString("status"));
  }

  @Test
  public void ChangingStatusRemovesExpiration() throws IOException {
    String reqBody = "{\"status\": \"draft\"}";
    mockReq.setBodyContent(reqBody);
    mockReq.setMethod("PATCH");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/:id", ImmutableMap.of("id", samsNoteId.toHexString()));
    noteController.editNote(ctx);

    assertEquals(204, mockRes.getStatus());

    assertEquals(1, db.getCollection("notes").countDocuments(eq("_id", samsNoteId)));

    Document editedNote = db.getCollection("notes").find(eq("_id", samsNoteId)).first();
    assertNotNull(editedNote);

    assertEquals("draft", editedNote.getString("status"));
    assertNull(editedNote.getString("expireDate"));

    assertEquals("I am sam", editedNote.getString("body"));
    assertEquals("owner3_ID", editedNote.getString("ownerID"));
    assertEquals("2020-03-07T22:03:38+0000", editedNote.getString("addDate"));

  }

  @Test
  public void AddNewInactiveWithExpiration() throws IOException {
    String testNewNote = "{ " + "\"ownerID\": \"e7fd674c72b76596c75d9f1e\", " + "\"body\": \"Test Body\", "
        + "\"addDate\": \"2020-03-07T22:03:38+0000\", " + "\"expireDate\": \"2021-03-07T22:03:38+0000\", "
        + "\"status\": \"draft\" }";

    mockReq.setBodyContent(testNewNote);
    mockReq.setMethod("POST");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/new");

    assertThrows(ConflictResponse.class, () -> {
      noteController.addNewNote(ctx);
    });
  }

  @Test
  public void AddExpirationToInactive() throws IOException {

    String testNewNote = "{ " + "\"ownerID\": \"e7fd674c72b76596c75d9f1e\", " + "\"body\": \"Test Body\", "
        + "\"addDate\": \"2020-03-07T22:03:38+0000\", " + "\"status\": \"template\" }";

    mockReq.setBodyContent(testNewNote);
    mockReq.setMethod("POST");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/new");

    noteController.addNewNote(ctx);

    String id = jsonMapper.readValue(ctx.resultString(), ObjectNode.class).get("id").asText();
    mockRes.resetAll();

    mockReq.setBodyContent("{\"expireDate\": \"2021-03-07T22:03:38+0000\"}");
    mockReq.setMethod("PATCH");
    Context ctx2 = ContextUtil.init(mockReq, mockRes, "api/notes/:id",
        ImmutableMap.of("id", new ObjectId(id).toHexString()));

    assertThrows(ConflictResponse.class, () -> {
      noteController.editNote(ctx2);
    });

  }

  @Test
  public void AddExpirationAndDeactivate() throws IOException {
    mockReq.setBodyContent("{\"expireDate\": \"2021-03-07T22:03:38+0000\", \"status\": \"draft\"}");
    mockReq.setMethod("PATCH");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes/:id", ImmutableMap.of("id", samsNoteId.toHexString()));

    assertThrows(ConflictResponse.class, () -> {
      noteController.editNote(ctx);
    });
  }
}
