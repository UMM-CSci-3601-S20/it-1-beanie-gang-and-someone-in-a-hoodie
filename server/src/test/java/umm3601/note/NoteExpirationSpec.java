package umm3601.note;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
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
import org.mockito.Mock;
import org.mockito.Spy;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.util.ContextUtil;
import io.javalin.plugin.json.JavalinJson;
import umm3601.UnprocessableResponse;

import java.util.Timer;
import java.util.TimerTask;

public class NoteExpirationSpec {
  MockHttpServletRequest mockReq = new MockHttpServletRequest();
  MockHttpServletResponse mockRes = new MockHttpServletResponse();

  Note sampleNote;
  String sampleNoteID;

  private NoteController noteController;

  @Spy
  private DeathTimer mockDeathTimer;

  @BeforeEach
  public void setupEach() throws IOException {
    mockReq.resetAll();
    mockRes.resetAll();

    sampleNote = new Note();
    sampleNoteID = new ObjectId().toHexString();
    sampleNote._id = sampleNoteID;
    sampleNote.addDate = "2020-03-07T22:03:38+0000";
    sampleNote.ownerID = "sampleOwnerID";
    sampleNote.status = "active";

  }

  //Tests to internal timer functionality




  //Tests to tasks





}
