package umm3601.note;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.Resource;

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

    @Resource DeathTimer dTimer = getDeathTimerInstance();

    public ExpireTask
  }
}
