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

  protected HashMap<String, ? extends TimerTask> pendingDeletion = new HashMap<String, TimerTask>();

  public boolean scheduleExpiration(Note n) {
    return false;
  }

  public boolean schedulePurge(Note n) {
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
