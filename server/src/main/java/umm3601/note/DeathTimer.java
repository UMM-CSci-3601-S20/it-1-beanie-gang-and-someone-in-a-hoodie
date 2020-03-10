package umm3601.note;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;


public class DeathTimer extends Timer {


  private static DeathTimer deathTimerInstance = new DeathTimer();

  private DeathTimer() {};

  public static DeathTimer getDeathTimerInstance() {
    return deathTimerInstance;
  }


  //id:task
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
  public boolean updateTimerStatus(Note n) {
    return false;
  }

  public class ExpireTask extends TimerTask {

    @Inject
    DeathTimer dTimer = getDeathTimerInstance();

    public ExpireTask (Note n) {

    }

    @Override
    public void run() {

    }

  }

  public class PurgeTask extends TimerTask {


    @Inject
    DeathTimer dTimer = getDeathTimerInstance();

    public PurgeTask (Note n) {

    }

    @Override
    public void run() {

    }

  }
}
