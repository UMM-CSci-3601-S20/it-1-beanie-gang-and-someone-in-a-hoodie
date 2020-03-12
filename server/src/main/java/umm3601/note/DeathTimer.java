package umm3601.note;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.mongodb.client.MongoDatabase;



@Singleton
public class DeathTimer extends Timer {

  public final long ONE_WEEK_MILLIS = 604800000; // 1 week, in milliseconds
  public final long DELETED_POST_PURGE_DELAY = ONE_WEEK_MILLIS;
  // In order to make it more obvious what to change, if the customer
  // wants deleted notes to 'linger' for a different length of time.

  private NoteController noteController;

  @Inject
  void NoteControllerSetup(MongoDatabase db) {
    noteController = new NoteController(db, this);
  }

  @Inject
  private static DeathTimer deathTimerInstance = new DeathTimer();

  protected DeathTimer() {
    super(true);  // Start as a daemon
  }
  // Made not private, with the idea that it'll be injected in actual usage.

  public static DeathTimer getDeathTimerInstance() {
    return deathTimerInstance;
  }

  // maps note id to task
  protected HashMap<String, TimerTask> pendingDeletion = new HashMap<String, TimerTask>();

  DateFormat df = new StdDateFormat();

  /**
   * Given a single note, correctly sets up timers for its expiration.
   *
   * This function behaves slightly differently for different types of note. Any
   * existing timer will be purged if applicable. Then, notes with an expiration
   * date (which are understood to be only active dates) will have a timer set
   * which flags them as deleted at that date, and deleted notes will have a timer
   * set before they are permanently purged from the database. This timer is
   * currently set to one week.
   *
   * @param n: The note whose timer will be updated.
   * @return true if the note now has a timer attached to it, false otherwise.
   */
  public boolean updateTimerStatus(Note n) {
    String noteStatus = n.status;
    String noteId = n._id;
    Boolean output = false;
    TimerTask timerTask;
    clearKey(noteId);

    if (noteStatus.equals("deleted")) {
      timerTask = new PurgeTask(noteId);
      pendingDeletion.put(noteId, timerTask);
      schedule(timerTask, DELETED_POST_PURGE_DELAY);
      output = true;
    } else if (noteStatus.equals("active") && n.expireDate != null) {
      Date expiration;
      try {
        expiration = df.parse(n.expireDate);
      } catch (ParseException e) {
        throw new IllegalArgumentException("Unable to parse the note's date", e);
      }
      timerTask = new ExpireTask(noteId);
      pendingDeletion.put(noteId, timerTask);
      schedule(timerTask, expiration);
      output = true;
    }
    return output;
  }

  public boolean clearKey(String s) {
    boolean output = false;
    if (pendingDeletion.containsKey(s)) {
      output = pendingDeletion.get(s).cancel();
      pendingDeletion.remove(s);
    }
    return output;
  }

  /**
   * Completely abandons all pending tasks, cancelling their deletion.
   *
   * This should almost certainly not be used in production; it will cancel all
   * pending deletions with no notification and no indication that the notices in
   * question will not be deleted or purged.
   */
  public void clearAllPending() {
    pendingDeletion.forEach((String s, TimerTask tt) -> {
      tt.cancel();
    });
    pendingDeletion.clear();
  }

  public class ExpireTask extends TimerTask {

    String target;

    public ExpireTask(Note n) {
      target = n._id;
    }

    public ExpireTask(String noteId) {
      target = noteId;
    }

    @Override
    public void run() {
      noteController.flagOneForDeletion(target);
    }
  }

  public class PurgeTask extends TimerTask {

    String target;

    public PurgeTask(Note n) {
      target = n._id;
    }

    public PurgeTask(String noteId) {
      target = noteId;
    }

    @Override
    public void run() {
      noteController.singleDelete(target);
    }

  }
}
