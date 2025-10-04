package net.hollowed.combatamenities.util.delay;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TickDelayScheduler {
    public static final Map<Integer, DelayHandler> scheduledTasks = new ConcurrentHashMap<>();
    private static int taskIdCounter = 0;

    /**
     * Schedules a task to run after a delay in server ticks.
     *
     * @param ticks Delay in ticks before execution.
     * @param task  Code to run after the delay.
     * @return Task ID (can be used to cancel).
     */
    @SuppressWarnings("all")
    public static int schedule(int ticks, Runnable task) {
        int taskId = taskIdCounter++;

        scheduledTasks.put(taskId, new DelayHandler(ticks, task, taskId));
        return taskId;
    }

    /**
     * Called every tick to process scheduled tasks.
     */
    public static void tick() {
        if (!scheduledTasks.isEmpty()) {
            for (int i = 0; i < scheduledTasks.size(); i++) {
                DelayHandler handler = scheduledTasks.values().stream().toList().get(i);
                if (handler.ticks > 0) {
                    handler.ticks--;
                } else {
                    handler.task.run();
                    scheduledTasks.remove(handler.id);
                    if (scheduledTasks.isEmpty()) break;
                }
            }
        }
    }

    /**
     * Cancels a scheduled task by ID.
     *
     * @param taskId ID of the task.
     */
    @SuppressWarnings("unused")
    public static void cancel(int taskId) {
        scheduledTasks.remove(taskId);
    }
}
