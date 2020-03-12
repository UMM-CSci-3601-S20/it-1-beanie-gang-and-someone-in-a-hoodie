package umm3601.owner;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
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


public class OwnerControllerSpec{

  MockHttpServletRequest mockReq = new MockHttpServletRequest();
  MockHttpServletResponse mockRes = new MockHttpServletResponse();

  private OwnerController ownerController;

  private ObjectId samsId;

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

    MongoCollection<Document> ownerDocuments = db.getCollection("owners");
    ownerDocuments.drop();
    List<Document> testOwners = new ArrayList<>();
    testOwners.add(Document.parse(
      "{\n" +
      " name: \"Billy \" ,\n" +
      " building: \"Wild \" ,\n" +
      " officeNumber: \"1234 \" ,\n" +
      " email: \"billythekid@this.that \" \n" +
      "}"
    ));
    testOwners.add(Document.parse(
      "{\n" +
      " name: \"George Washington \" ,\n" +
      " building: \"White House \" ,\n" +
      " officeNumber: \"1789 \" ,\n" +
      " email: \"revolution@freedom.us.fake \" \n" +
      "}"
    ));
    testOwners.add(Document.parse(
      "{\n" +
      " name: \"Not a cat \" ,\n" +
      " building: \"Building for people\" ,\n" +
      " officeNumber: \"1111 \", \n" +
      " email: \"totallynotacat@eatmice.com \" \n" +
      "}"
    ));

    samsId = new ObjectId();
    BasicDBObject sam = new BasicDBObject("_id", samsId);
    sam = sam.append("name", "Sam")
    .append("building", "HFA")
    .append("officeNumber", "23")
    .append("email", "sam@frogs.com");

    ownerDocuments.insertMany(testOwners);
    ownerDocuments.insertOne(Document.parse(sam.toJson()));

    ownerController = new OwnerController(db);
}

  @AfterAll
  public static void teardown(){
    db.drop();
    mongoClient.close();
  }

  @Test
  public void GetAllOwners(){
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/owners");
    ownerController.getOwners(ctx);

    assertEquals(200, mockRes.getStatus());

    String result = ctx.resultString();
    assertEquals(
      db.getCollection("owners").countDocuments(),
      JavalinJson.fromJson(result, Owner[].class).length,
      "Wrong number of entries"
      );
  }

  @Test
  public void GetOwnerWithExistentId() throws IOException {

    String testID = samsId.toHexString();

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/owners/:id", ImmutableMap.of("id", testID));
    ownerController.getOwner(ctx);

    assertEquals(200, mockRes.getStatus());

    String result = ctx.resultString();
    Owner resultOwner = JavalinJson.fromJson(result, Owner.class);

    assertEquals(resultOwner._id, samsId.toHexString());
    assertEquals(resultOwner.name, "Sam");
    assertEquals(resultOwner.email, "sam@frogs.com");
  }
  @Test
  public void GetOwnerWithBadId() throws IOException {

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/owners/:id", ImmutableMap.of("id", "bad"));

    assertThrows(BadRequestResponse.class, () -> {
      ownerController.getOwner(ctx);
    });
  }

  @Test
  public void GetOwnerWithNonexistentId() throws IOException {

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/owners/:id", ImmutableMap.of("id", "58af3a600343927e48e87335"));

    assertThrows(NotFoundResponse.class, () -> {
      ownerController.getOwner(ctx);
    });
  }

  @Test
  public void AddOwner() throws IOException {

    String testNewOwner = "{\"name\": \"Test Owner\", "
    + "\"building\": \"place\", "
    + "\"officeNumber\": \"0000\", "
    + "\"email\": \"test@example.com\" }";

    mockReq.setBodyContent(testNewOwner);
    mockReq.setMethod("POST");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/owners/new");

    ownerController.addNewOwner(ctx);

    assertEquals(201, mockRes.getStatus());

    String result = ctx.resultString();
    String id = jsonMapper.readValue(result, ObjectNode.class).get("id").asText();
    assertNotEquals("", id);

    assertEquals(1, db.getCollection("owners").countDocuments(eq("_id", new ObjectId(id))));

    //verify owner was added to the database and the correct ID
    Document addedOwner = db.getCollection("owners").find(eq("_id", new ObjectId(id))).first();
    assertNotNull(addedOwner);
    assertEquals("Test Owner", addedOwner.getString("name"));
    assertEquals("place", addedOwner.getString("building"));
    assertEquals("0000", addedOwner.getString("officeNumber"));
    assertEquals("test@example.com", addedOwner.getString("email"));
  }
  @Test
  public void AddInvalidEmailOwner() throws IOException {
    String testNewOwner = "{\n\t\"name\": \"Test Owner\",\n\t\"building\": \"place\",\n\t\"officeNumber\": \"5432\",\n\t\"email\": \"invalidemail\" }";
    mockReq.setBodyContent(testNewOwner);
    mockReq.setMethod("POST");
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/owners/new");

    assertThrows(BadRequestResponse.class, () -> {
      ownerController.addNewOwner(ctx);
    });
}
  @Test
  public void AddInvalidNameOwner() throws IOException{
    String testNewOwner = "{"
    + "\"building\": \"place\", "
    + "\"officeNumber\": \"0000\", "
    + "\"email\": \"test@example.com\" }";

    mockReq.setBodyContent(testNewOwner);
    mockReq.setMethod("POST");
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/owners/new");

    assertThrows(BadRequestResponse.class, () -> {
      ownerController.addNewOwner(ctx);
    });
  }
}
