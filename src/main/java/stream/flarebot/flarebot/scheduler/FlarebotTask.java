package stream.flarebot.flarebot.scheduler;

/**
 * ayy it repeats
 * <br>
 * Created by Arsen on 20.9.16..
 */
public abstract class FlarebotTask implements Runnable {

    private String taskName;

    private FlareBotTask() {}

    public FlarebotTask(String taskName) {
        this.taskName = taskName;
    }

    public boolean repeat(long delay, long interval) {
        return Scheduler.scheduleRepeating(this, taskName, delay, interval);
    }

    public void delay(long delay) {
        Scheduler.delayTask(this, taskName, delay);
    }

    public boolean cancel() {
        return Scheduler.cancelTask(taskName);
    }
}
