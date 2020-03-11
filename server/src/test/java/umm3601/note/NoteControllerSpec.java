package umm3601.note;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.util.ContextUtil;
import io.javalin.plugin.json.JavalinJson;

public class NoteControllerSpec{

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
    mongoClient = MongoClients.create(
      MongoClientSettings.builder()
      .applyToClusterSettings(builder ->
      builder.hosts(Arrays.asList(new ServerAddress(mongoAddr))))
      .build());

    db = mongoClient.getDatabase("test");
  }

  @BeforeEach
  public void setupEach()throws IOException{
     // Reset our mock request and response objects
     mockReq.resetAll();
     mockRes.resetAll();

     MongoCollection<Document> noteDocuments = db.getCollection("notes");
     noteDocuments.drop();
     List<Document> testNotes = new ArrayList<>();
     testNotes.add(Document.parse("{ "
     + "ownerID: \"owner1_ID\", "
     + "body: \"I am running 5 minutes late to my non-existent office\", "
     + "addDate: \"2020-03-07T22:03:38+0000\", "
     + "expireDate: \"2021-03-20T22:03:38+0000\", "
     + "status: \"active\""
     +"}"
     ));
     testNotes.add(Document.parse("{ "
     + "ownerID: \"owner1_ID\", "
     + "body: \"I am never coming to my office again\", "
     + "addDate: \"2020-03-07T22:03:38+0000\", "
     + "expireDate: \"2099-03-07T22:03:38+0000\", "
     + "status: \"active\""
     +"}"
     ));
     testNotes.add(Document.parse("{ "
     + "ownerID: \"owner2_ID\", "
     + "body: \"I am on sebatacle no office hours\", "
     + "addDate: \"2020-03-07T22:03:38+0000\", "
     + "expireDate: \"2021-03-07T22:03:38+0000\", "
     + "status: \"active\""
     +"}"
     ));
     testNotes.add(Document.parse("{ "
     + "ownerID: \"owner2_ID\", "
     + "body: \"Go to owner3's office\", "
     + "addDate: \"2020-03-07T22:03:38+0000\", "
     + "expireDate: \"2020-03-21T22:03:38+0000\", "
     + "status: \"active\""
     +"}"
     ));
     testNotes.add(Document.parse("{ "
     + "ownerID: \"owner3_ID\", "
     + "body: \"Not many come to my office I offer donuts\", "
     + "addDate: \"2020-03-07T22:03:38+0000\", "
     + "expireDate: \"2021-03-07T22:03:38+0000\", "
     + "status: \"active\""
     +"}"
     ));
    samsNoteId = new ObjectId();
    BasicDBObject sam = new BasicDBObject("_id",samsNoteId);
    sam = sam.append("ownerID", "owner3_ID")
    .append("body", "I am sam")
    .append("addDate","2020-03-07T22:03:38+0000")
    .append("expireDate", "2100-03-07T22:03:38+0000")
    .append("status", "active");

    noteDocuments.insertMany(testNotes);
    noteDocuments.insertOne(Document.parse(sam.toJson()));

    noteController = new NoteController(db);
  }

  @AfterAll
  public static void teardown(){
    db.drop();
    mongoClient.close();
  }

  @Test
  public void getAllNotesForOwner1(){
    mockReq.setQueryString("ownerid=owner1_ID");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/notes");

    noteController.getNotesByOwner(ctx);

    assertEquals(200, mockRes.getStatus());

    String result = ctx.resultString();
    Note[] resultNotes = JavalinJson.fromJson(result, Note[].class);

    assertEquals(2, resultNotes.length);
    for(Note note : resultNotes){
      assertEquals("owner1_ID", note.ownerID, "Incorrect ID");
    }
  }

  @Test
  public void addNote() throws IOException{
    String testNewNote = "{\n "
    + "\"ownerID\": \"e7fd674c72b76596c75d9f1e\", "
    + "\"body\": \"Test Body\", "
    + "\"addDate\": \"2020-03-07T22:03:38+0000\", "
    + "\"expireDate\": \"2021-03-07T22:03:38+0000\" , "
    + "\"status\": \"active\"\n }";

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

    Document addedNote = db.getCollection("notes").find(eq("_id",new ObjectId(id))).first();
    assertNotNull(addedNote);
    assertEquals("e7fd674c72b76596c75d9f1e", addedNote.getString("ownerID"));
    assertEquals("Test Body", addedNote.getString("body"));
    assertEquals("2020-03-07T22:03:38+0000", addedNote.getString("addDate"));
    assertEquals("2021-03-07T22:03:38+0000", addedNote.getString("expireDate"));
    assertEquals("active", addedNote.getString("status"));
  }
}
