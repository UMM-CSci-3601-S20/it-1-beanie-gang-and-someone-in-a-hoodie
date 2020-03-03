package umm3601.user;

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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.util.ContextUtil;
import io.javalin.plugin.json.JavalinJson;


/**
* Tests the logic of the UserController
*
* @throws IOException
*/
public class UserControllerSpec {

  MockHttpServletRequest mockReq = new MockHttpServletRequest();
  MockHttpServletResponse mockRes = new MockHttpServletResponse();

  private UserController userController;

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
  public void setupEach() throws IOException {

    // Reset our mock request and response objects
    mockReq.resetAll();
    mockRes.resetAll();

    // Setup database
    MongoCollection<Document> userDocuments = db.getCollection("users");
    userDocuments.drop();
    List<Document> testUsers = new ArrayList<>();
    testUsers.add(Document.parse("{\n" +
    "                    name: \"Chris\",\n" +
    "                    age: 25,\n" +
    "                    company: \"UMM\",\n" +
    "                    email: \"chris@this.that\",\n" +
    "                    role: \"admin\",\n" +
    "                    avatar: \"https://gravatar.com/avatar/8c9616d6cc5de638ea6920fb5d65fc6c?d=identicon\"\n" +
    "                }"));
    testUsers.add(Document.parse("{\n" +
    "                    name: \"Pat\",\n" +
    "                    age: 37,\n" +
    "                    company: \"IBM\",\n" +
    "                    email: \"pat@something.com\",\n" +
    "                    role: \"editor\",\n" +
    "                    avatar: \"https://gravatar.com/avatar/b42a11826c3bde672bce7e06ad729d44?d=identicon\"\n" +
    "                }"));
    testUsers.add(Document.parse("{\n" +
    "                    name: \"Jamie\",\n" +
    "                    age: 37,\n" +
    "                    company: \"OHMNET\",\n" +
    "                    email: \"jamie@frogs.com\",\n" +
    "                    role: \"viewer\",\n" +
    "                    avatar: \"https://gravatar.com/avatar/d4a6c71dd9470ad4cf58f78c100258bf?d=identicon\"\n" +
    "                }"));

    samsId = new ObjectId();
    BasicDBObject sam = new BasicDBObject("_id", samsId);
    sam = sam.append("name", "Sam")
      .append("age", 45)
      .append("company", "OHMNET")
      .append("email", "sam@frogs.com")
      .append("role", "viewer")
      .append("avatar", "https://gravatar.com/avatar/08b7610b558a4cbbd20ae99072801f4d?d=identicon");


    userDocuments.insertMany(testUsers);
    userDocuments.insertOne(Document.parse(sam.toJson()));

    userController = new UserController(db);
  }

  @AfterAll
  public static void teardown() {
    db.drop();
    mongoClient.close();
  }

  @Test
  public void GetAllUsers() throws IOException {

    // Create our fake Javalin context
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users");
    userController.getUsers(ctx);


    assertEquals(200, mockRes.getStatus());

    String result = ctx.resultString();
    assertEquals(db.getCollection("users").countDocuments(), JavalinJson.fromJson(result, User[].class).length);
  }

  @Test
  public void GetUsersByAge() throws IOException {

    // Set the query string to test with
    mockReq.setQueryString("age=37");

    // Create our fake Javalin context
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users");

    userController.getUsers(ctx);

    assertEquals(200, mockRes.getStatus()); // The response status should be 200

    String result = ctx.resultString();
    User[] resultUsers = JavalinJson.fromJson(result, User[].class);

    assertEquals(2, resultUsers.length); // There should be two users returned
    for (User user : resultUsers) {
      assertEquals(37, user.age); // Every user should be age 37
    }
  }

  /**
  * Test that if the user sends a request with an illegal value in
  * the age field (i.e., something that can't be parsed to a number)
  * we get a reasonable error code back.
  */
  @Test
  public void GetUsersWithIllegalAge() {

    mockReq.setQueryString("age=abc");
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users");

    // This should now throw a `BadRequestResponse` exception because
    // our request has an age that can't be parsed to a number.
    assertThrows(BadRequestResponse.class, () -> {
      userController.getUsers(ctx);
    });
  }

  @Test
  public void GetUsersByCompany() throws IOException {

    mockReq.setQueryString("company=OHMNET");
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users");
    userController.getUsers(ctx);

    assertEquals(200, mockRes.getStatus());
    String result = ctx.resultString();

    User[] resultUsers = JavalinJson.fromJson(result, User[].class);

    assertEquals(2, resultUsers.length); // There should be two users returned
    for (User user : resultUsers) {
      assertEquals("OHMNET", user.company);
    }
  }

  @Test
  public void GetUsersByRole() throws IOException {

    mockReq.setQueryString("role=viewer");
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users");
    userController.getUsers(ctx);

    assertEquals(200, mockRes.getStatus());
    String result = ctx.resultString();
    for (User user : JavalinJson.fromJson(result, User[].class)) {
      assertEquals("viewer", user.role);
    }
  }

  @Test
  public void GetUsersByCompanyAndAge() throws IOException {

    mockReq.setQueryString("company=OHMNET&age=37");
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users");
    userController.getUsers(ctx);

    assertEquals(200, mockRes.getStatus());
    String result = ctx.resultString();
    User[] resultUsers = JavalinJson.fromJson(result, User[].class);

    assertEquals(1, resultUsers.length); // There should be one user returned
    for (User user : resultUsers) {
       assertEquals("OHMNET", user.company);
       assertEquals(37, user.age);
     }
  }

  @Test
  public void GetUserWithExistentId() throws IOException {

    String testID = samsId.toHexString();

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users/:id", ImmutableMap.of("id", testID));
    userController.getUser(ctx);

    assertEquals(200, mockRes.getStatus());

    String result = ctx.resultString();
    User resultUser = JavalinJson.fromJson(result, User.class);

    assertEquals(resultUser._id, samsId.toHexString());
    assertEquals(resultUser.name, "Sam");
  }

  @Test
  public void GetUserWithBadId() throws IOException {

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users/:id", ImmutableMap.of("id", "bad"));

    assertThrows(BadRequestResponse.class, () -> {
      userController.getUser(ctx);
    });
  }

  @Test
  public void GetUserWithNonexistentId() throws IOException {

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users/:id", ImmutableMap.of("id", "58af3a600343927e48e87335"));

    assertThrows(NotFoundResponse.class, () -> {
      userController.getUser(ctx);
    });
  }

  @Test
  public void AddUser() throws IOException {

    String testNewUser = "{\n\t\"name\": \"Test User\",\n\t\"age\":25,\n\t\"company\": \"testers\",\n\t\"email\": \"test@example.com\",\n\t\"role\": \"viewer\"\n}";

    mockReq.setBodyContent(testNewUser);
    mockReq.setMethod("POST");

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users/new");

    userController.addNewUser(ctx);

    assertEquals(201, mockRes.getStatus());

    String result = ctx.resultString();
    String id = jsonMapper.readValue(result, ObjectNode.class).get("id").asText();
    assertNotEquals("", id);
    System.out.println(id);

    assertEquals(1, db.getCollection("users").countDocuments(eq("_id", new ObjectId(id))));

    //verify user was added to the database and the correct ID
    Document addedUser = db.getCollection("users").find(eq("_id", new ObjectId(id))).first();
    assertNotNull(addedUser);
    assertEquals("Test User", addedUser.getString("name"));
    assertEquals(25, addedUser.getInteger("age"));
    assertEquals("testers", addedUser.getString("company"));
    assertEquals("test@example.com", addedUser.getString("email"));
    assertEquals("viewer", addedUser.getString("role"));
    assertTrue(addedUser.containsKey("avatar"));
  }

  @Test
  public void AddInvalidEmailUser() throws IOException {
    String testNewUser = "{\n\t\"name\": \"Test User\",\n\t\"age\":25,\n\t\"company\": \"testers\",\n\t\"email\": \"invalidemail\",\n\t\"role\": \"viewer\"\n}";
    mockReq.setBodyContent(testNewUser);
    mockReq.setMethod("POST");
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users/new");

    assertThrows(BadRequestResponse.class, () -> {
      userController.addNewUser(ctx);
    });
  }

  @Test
  public void AddInvalidAgeUser() throws IOException {
    String testNewUser = "{\n\t\"name\": \"Test User\",\n\t\"age\":\"notanumber\",\n\t\"company\": \"testers\",\n\t\"email\": \"test@example.com\",\n\t\"role\": \"viewer\"\n}";
    mockReq.setBodyContent(testNewUser);
    mockReq.setMethod("POST");
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users/new");

    assertThrows(BadRequestResponse.class, () -> {
      userController.addNewUser(ctx);
    });
  }

  @Test
  public void AddInvalidNameUser() throws IOException {
    String testNewUser = "{\n\t\"age\":25,\n\t\"company\": \"testers\",\n\t\"email\": \"test@example.com\",\n\t\"role\": \"viewer\"\n}";
    mockReq.setBodyContent(testNewUser);
    mockReq.setMethod("POST");
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users/new");

    assertThrows(BadRequestResponse.class, () -> {
      userController.addNewUser(ctx);
    });
  }

  @Test
  public void AddInvalidRoleUser() throws IOException {
    String testNewUser = "{\n\t\"name\": \"Test User\",\n\t\"age\":25,\n\t\"company\": \"testers\",\n\t\"email\": \"test@example.com\",\n\t\"role\": \"invalidrole\"\n}";
    mockReq.setBodyContent(testNewUser);
    mockReq.setMethod("POST");
    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users/new");

    assertThrows(BadRequestResponse.class, () -> {
      userController.addNewUser(ctx);
    });
  }

  @Test
  public void DeleteUser() throws IOException {

    String testID = samsId.toHexString();

    // User exists before deletion
    assertEquals(1, db.getCollection("users").countDocuments(eq("_id", new ObjectId(testID))));

    Context ctx = ContextUtil.init(mockReq, mockRes, "api/users/:id", ImmutableMap.of("id", testID));
    userController.deleteUser(ctx);

    assertEquals(200, mockRes.getStatus());

    // User is no longer in the database
    assertEquals(0, db.getCollection("users").countDocuments(eq("_id", new ObjectId(testID))));
  }

}
