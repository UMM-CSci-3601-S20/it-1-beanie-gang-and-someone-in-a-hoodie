package umm3601;

import java.util.Arrays;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import io.javalin.Javalin;
import umm3601.owner.OwnerController;
import umm3601.note.DeathTimer;
import umm3601.note.NoteController;
import umm3601.user.UserController;

public class Server {

  static String appName = "CSCI 3601 Iteration Template";

  private static MongoDatabase database;

  public static void main(String[] args) {

    // Get the MongoDB address and database name from environment variables and
    // if they aren't set, use the defaults of "localhost" and "dev".
    String mongoAddr = System.getenv().getOrDefault("MONGO_ADDR", "localhost");
    String databaseName = System.getenv().getOrDefault("MONGO_DB", "team4IterationDev");

    // Setup the MongoDB client object with the information we set earlier
    MongoClient mongoClient = MongoClients.create(
      MongoClientSettings.builder()
      .applyToClusterSettings(builder ->
        builder.hosts(Arrays.asList(new ServerAddress(mongoAddr))))
      .build());

    // Get the database
    database = mongoClient.getDatabase(databaseName);

    // Initialize dependencies
    UserController userController = new UserController(database);
    OwnerController ownerController = new OwnerController(database);
    NoteController noteController = new NoteController(database, DeathTimer.getDeathTimerInstance());
    //UserRequestHandler userRequestHandler = new UserRequestHandler(userController);

    Javalin server = Javalin.create().start(4567);

    // Simple example route
    server.get("hello", ctx -> ctx.result("Hello World"));

    // Utility routes
    server.get("api", ctx -> ctx.result(appName));

    // Get specific user
    server.get("api/users/:id", userController::getUser);

    server.delete("api/users/:id", userController::deleteUser);

    // List users, filtered using query parameters
    server.get("api/users", userController::getUsers);

    // Add new user
    server.post("api/users/new", userController::addNewUser);


    // ----- Owner routes ----- //
    // Get specific owner
    server.get("api/owners/:id", ownerController::getOwner);

    // Delete specific owner
    server.delete("api/owners/:id", ownerController::deleteOwner);

    // List owners, filtered using query parameters
    server.get("api/owners", ownerController::getOwners);

    // Add new owner
    server.post("api/owners/new", ownerController::addNewOwner);


    // ----- Note routes ----- //
    // Get specific note
    server.get("api/notes/:id", noteController::getNoteById);

    // Delete specific note
    server.delete("api/notes/:id", noteController::deleteNote);

    // List notes, filtered using query parameters
    server.get("api/notes", noteController::getNotesByOwner);

    // Add new note
    server.post("api/notes/new", noteController::addNewNote);

    // Update a note
    server.patch("api/notes/:id", noteController::editNote);

    server.exception(Exception.class, (e, ctx) -> {
      ctx.status(500);
      ctx.json(e); // you probably want to remove this in production
    });
  }
}
