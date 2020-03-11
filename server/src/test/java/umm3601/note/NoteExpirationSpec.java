package umm3601.note;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.security.acl.Owner;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.s;
import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.util.ContextUtil;
import io.javalin.plugin.json.JavalinJson;
import umm3601.UnprocessableResponse;
import umm3601.note.DeathTimer.*;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

@RunWith(MockitoJUnitRunner.class)
public class NoteExpirationSpec {
  MockHttpServletRequest mockReq = new MockHttpServletRequest();
  MockHttpServletResponse mockRes = new MockHttpServletResponse();

  Note sampleNote;
  String sampleNoteID;
  Note anotherNote;
  String anotherNoteID;
  Note thirdNote;
  String thirdNoteID;
  Note fourthNote;
  String fourthNoteID;
  DateFormat df;
  Date expirationDate;

  @Mock
  private NoteController mockNoteController;

  @Spy
  private DeathTimer spyDeathTimer;

  @BeforeEach
  public void setupEach() throws IOException {
    mockReq.resetAll();
    mockRes.resetAll();
    spyDeathTimer.clearAllPending();
    reset(spyDeathTimer); // Necessary to work around deathTimer being a singleton
    mockNoteController = mock(NoteController.class);

    sampleNote = new Note();
    sampleNoteID = new ObjectId().toHexString();
    sampleNote._id = sampleNoteID;
    sampleNote.addDate = "2020-03-07T22:03:38+0000";
    sampleNote.ownerID = "sampleOwnerID";
    sampleNote.body = "Sample note body";
    sampleNote.status = "active";

    anotherNote = new Note();
    anotherNoteID = new ObjectId().toHexString();
    anotherNote._id = anotherNoteID;
    anotherNote.addDate = "2020-03-07T22:03:38+0000";
    anotherNote.ownerID = "anotherOwnerID";
    anotherNote.body = "Another note body";
    anotherNote.status = "deleted";

    thirdNote = new Note();
    thirdNoteID = new ObjectId().toHexString();
    thirdNote._id = thirdNoteID;
    thirdNote.addDate = "2022-04-03T22:03:38+0000";
    thirdNote.ownerID = "thirdOwnerID";
    thirdNote.body = "A third note body";
    thirdNote.status = "template";

    fourthNote = new Note();
    fourthNoteID = new ObjectId().toHexString();
    fourthNote._id = fourthNoteID;
    fourthNote.addDate = "2022-05-05T22:03:38+0000";
    fourthNote.expireDate = "2023-05-05T22:06:00+0000";
    fourthNote.ownerID = "fourthOwnerID";
    fourthNote.body = "A fourth note body";
    fourthNote.status = "active";

    expirationDate = new Date();

  }

  @AfterAll
  public void teardown() {
    spyDeathTimer.cancel();
  }

  // ** Tests to internal timer functionality **


  @Test
  public void AddNewTimer() throws IOException {
    try {
      sampleNote.expireDate = "2021-03-07T22:03:38+0000";
      expirationDate = df.parse("2021-03-07T22:03:38+0000");
    } catch (ParseException e) {
      fail("The provided date string 2021-03-07T22:03:38+0000 couldn't be parsed.");
    }

    assertTrue(spyDeathTimer.updateTimerStatus(sampleNote));
    assertEquals(1, spyDeathTimer.pendingDeletion.size());
    assertTrue(spyDeathTimer.pendingDeletion.containsKey(sampleNoteID));
    TimerTask newTask = spyDeathTimer.pendingDeletion.get(sampleNoteID);
    assertEquals(ExpireTask.class, newTask.getClass());
    assertEquals(((ExpireTask) newTask).target, sampleNoteID);
    verify(spyDeathTimer).schedule(newTask, expirationDate);
  }

  @Test
  public void RemoveTimer() throws IOException {
    sampleNote.expireDate = "2021-03-07T22:03:38+0000";
    assertTrue(spyDeathTimer.updateTimerStatus(sampleNote));
    TimerTask newTask = spyDeathTimer.pendingDeletion.get(sampleNoteID);

    sampleNote.expireDate = null;
    assertFalse(spyDeathTimer.updateTimerStatus(sampleNote));
    assertEquals(0, spyDeathTimer.pendingDeletion.size());
    assertFalse(newTask.cancel());
    verify(spyDeathTimer, times(1)).schedule(newTask, any(Date.class));
  }

  @Test
  public void NoTimer() throws IOException {
    assertFalse(spyDeathTimer.updateTimerStatus(sampleNote));
    assertEquals(0, spyDeathTimer.pendingDeletion.size());
    verify(spyDeathTimer, never()).schedule(any(TimerTask.class), any(Date.class));
  }

  @Test
  public void ChangeTimer() throws IOException {
    try {
    sampleNote.expireDate = "2021-03-07T22:03:38+0000";
    assertTrue(spyDeathTimer.updateTimerStatus(sampleNote));
    sampleNote.expireDate = "2025-03-07T22:03:38+0000";
    assertTrue(spyDeathTimer.updateTimerStatus(sampleNote));

    assertEquals(1, spyDeathTimer.pendingDeletion.size());
    assertTrue(spyDeathTimer.pendingDeletion.containsKey(sampleNoteID));
    TimerTask newTask = spyDeathTimer.pendingDeletion.get(sampleNoteID);
    assertEquals(ExpireTask.class, newTask.getClass());
    assertEquals(((ExpireTask) newTask).target, sampleNoteID);
    verify(spyDeathTimer).schedule(any(ExpireTask.class), df.parse("2021-03-07T22:03:38+0000"));
    verify(spyDeathTimer).schedule(newTask, df.parse("2025-03-07T22:03:38+0000"));

    } catch (ParseException e) {
      fail("The provided date strings couldn't be parsed.");
    }
  }

  @Test
  public void SingleTimerUnchanged() throws IOException {
    try {
      sampleNote.expireDate = "2021-03-07T22:03:38+0000";
      expirationDate = df.parse("2021-03-07T22:03:38+0000");
    } catch (ParseException e) {
      fail("The provided date string 2021-03-07T22:03:38+0000 couldn't be parsed.");
    }

    assertTrue(spyDeathTimer.updateTimerStatus(sampleNote));
    assertFalse(spyDeathTimer.updateTimerStatus(thirdNote));
    assertEquals(1, spyDeathTimer.pendingDeletion.size());
    assertTrue(spyDeathTimer.pendingDeletion.containsKey(sampleNoteID));
    TimerTask newTask = spyDeathTimer.pendingDeletion.get(sampleNoteID);
    assertEquals(ExpireTask.class, newTask.getClass());
    assertEquals(((ExpireTask) newTask).target, sampleNoteID);
    verify(spyDeathTimer).schedule(newTask, expirationDate);
  }

  @Test
  public void IndependentTimers() throws IOException {
    try{
      sampleNote.expireDate = "2021-03-07T22:03:38+0000";

      assertTrue(spyDeathTimer.updateTimerStatus(sampleNote));
      assertTrue(spyDeathTimer.updateTimerStatus(fourthNote));

      assertEquals(2, spyDeathTimer.pendingDeletion.size());
      assertTrue(spyDeathTimer.pendingDeletion.containsKey(sampleNoteID));
      assertTrue(spyDeathTimer.pendingDeletion.containsKey(fourthNoteID));
      TimerTask firstTask = spyDeathTimer.pendingDeletion.get(sampleNoteID);
      TimerTask secondTask = spyDeathTimer.pendingDeletion.get(fourthNoteID);

      assertEquals(ExpireTask.class, firstTask.getClass());
      assertEquals(ExpireTask.class, secondTask.getClass());
      assertEquals(((ExpireTask) firstTask).target, sampleNoteID);
      assertEquals(((ExpireTask) secondTask).target, fourthNoteID);

      verify(spyDeathTimer).schedule(firstTask, df.parse(sampleNote.expireDate));
      verify(spyDeathTimer).schedule(secondTask, df.parse(fourthNote.expireDate));
    } catch (ParseException e) {
      fail("Failed to parse date string.");
    }
  }



  //Tests to tasks





}
