package umm3601.note;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class DeathTimer extends Timer {

  public final long ONE_WEEK_MILLIS = 604800000; //1 week, in milliseconds

  public final long DELETED_POST_PURGE_DELAY = ONE_WEEK_MILLIS;
  //In order to make it more obvious what to change, if the customer
  //wants deleted notes to 'linger' for a different length of time.

  @Inject
  private NoteController noteController;

  private static DeathTimer deathTimerInstance = new DeathTimer();

  private DeathTimer() {super(true);} //Start as a daemon

  public static DeathTimer getDeathTimerInstance() {
    return deathTimerInstance;
  }


  //maps note id to task
  protected HashMap<String, ? extends TimerTask> pendingDeletion = new HashMap<String, TimerTask>();

  public void scheduleExpiration(Note n) {

  }

  public void schedulePurge(Note n) {

  }


  //In theory, this should be the only function needed in order to work.
  //It takes a note, then checks its status.  If it's not 'deleted', it kills
  //its corresponding task in pendingDeletion, then checks its expireDate.  If
  //that exists, it makes a new TimerTask scheduled for then.  SchedulePurge
  //might still need to be on its own, but I guess... if it's deleted, simply schedule
  //for 7 days?

  /**
   * Given a single note, correctly sets up timers for its expiration.
   *
   * This function behaves slightly differently for different types of note.
   * Any existing timer will be purged if applicable.  Then, notes with an
   * expiration date (which are understood to be only active dates) will
   * have a timer set which flags them as deleted at that date, and deleted
   * notes will have a timer set before they are permanently purged from the database.
   * This timer is currently set to one week.
   *
   * @param n: The note whose timer will be updated.
   * @return true if the note now has a timer attached to it, false otherwise.
   */
  public boolean updateTimerStatus(Note n) {
    return false;
  }

  /**
   * Completely abandons all pending tasks, cancelling their deletion.
   *
   * This should almost certainly not be used in production; it will cancel
   * all pending deletions with no notification and no indication that the
   * notices in question will not be deleted or purged.
   */
  public void clearAllPending() {
    pendingDeletion.forEach((String s, TimerTask tt) -> {
      tt.cancel();
      pendingDeletion.remove(s);
    });
  }



  public class ExpireTask extends TimerTask {

    String target;

    public ExpireTask (Note n) {
      target = n._id;
    }

    public ExpireTask (String noteId) {
      target = noteId;
    }


    @Override
    public void run() {

    }

  }

  public class PurgeTask extends TimerTask {

    String target;

    public PurgeTask (Note n) {
      target = n._id;
    }
    public PurgeTask (String noteId) {
      target = noteId;
    }


    @Override
    public void run() {

    }

  }
}
