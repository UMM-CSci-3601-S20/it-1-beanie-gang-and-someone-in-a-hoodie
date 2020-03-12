package umm3601.note;

import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import umm3601.note.DeathTimer.ExpireTask;
import umm3601.note.DeathTimer.PurgeTask;

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
