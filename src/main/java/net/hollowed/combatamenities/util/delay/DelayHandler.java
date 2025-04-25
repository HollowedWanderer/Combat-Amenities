package net.hollowed.combatamenities.util.delay;

public class DelayHandler {

    public int ticks;
    public Runnable task;
    public int id;

    public DelayHandler(int ticks, Runnable task, int id) {
        this.ticks = ticks;
        this.task = task;
        this.id = id;
    }
}
