package umm3601.note;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import umm3601.note.DeathTimer.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TimerTasksSpec {
  @Mock(name = "noteController") NoteController mockNoteController;

  @InjectMocks
   DeathTimer deathTimer;

  private static ObjectId samsNoteId;

  @BeforeEach
  public void setupEach() throws IOException {
    samsNoteId = new ObjectId();
    MockitoAnnotations.initMocks(this);
  }


  @Test
  public void runPurge() throws IOException {
    PurgeTask purgeTask = deathTimer.new PurgeTask(samsNoteId.toHexString());
    purgeTask.run();
    verify(mockNoteController).singleDelete(samsNoteId.toHexString());
  }

  @Test
  public void runFlag() throws IOException {
    ExpireTask expireTask = deathTimer.new ExpireTask(samsNoteId.toHexString());
    expireTask.run();
    verify(mockNoteController).flagOneForDeletion(samsNoteId.toHexString());
  }


}
