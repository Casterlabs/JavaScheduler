package co.casterlabs.javascheduler;

import java.util.concurrent.TimeUnit;

import lombok.NonNull;

public interface Scheduler {

    /* ---------------- */
    /* Timeout          */
    /* ---------------- */

    /**
     * Schedules a task to be run after the specified amount of time.
     * 
     * @param  task The task to be run.
     * 
     * @return      a task id for use in {@link #clearTimeout(int)}.
     */
    public int setTimeout(@NonNull Runnable task, long millis);

    /**
     * Cancels a timeout task.
     * 
     * @param taskId The task to cancel.
     */
    public void clearTimeout(int taskId);

    /* ---------------- */
    /* Interval         */
    /* ---------------- */

    /**
     * Schedules a task to be run continuously every specified period.
     * 
     * @param  task The task to be run.
     * 
     * @return      a task id for use in {@link #clearInterval(int)}.
     */
    public int setInterval(@NonNull Runnable task, long millis);

    /**
     * Cancels an interval task.
     * 
     * @param taskId The task to cancel.
     */
    public void clearInterval(int taskId);

    /* ---------------- */
    /* Utilities        */
    /* ---------------- */

    /**
     * Schedules a task to be run after the specified amount of time.
     * 
     * @param  task The task to be run.
     * 
     * @return      a task id for use in {@link #clearTimeout(int)}.
     */
    default int setTimeout(@NonNull Runnable task, long duration, @NonNull TimeUnit unit) {
        return this.setTimeout(task, unit.toMillis(duration));
    }

    /**
     * Schedules a task to be run continuously every specified period.
     * 
     * @param  task The task to be run.
     * 
     * @return      a task id for use in {@link #clearInterval(int)}.
     */
    default int setInterval(@NonNull Runnable task, long duration, @NonNull TimeUnit unit) {
        return this.setInterval(task, unit.toMillis(duration));
    }

    /* ---------------- */
    /* Static           */
    /* ---------------- */

    public static class Static {
        private static Scheduler scheduler = new StaticSchedulerImpl(); // Go here for more info about the impl.

        /**
         * Schedules a task to be run after the specified amount of time.
         * 
         * @param  task The task to be run.
         * 
         * @return      a task id for use in {@link #clearTimeout(int)}.
         */
        public static int setTimeout(@NonNull Runnable task, long millis) {
            return scheduler.setTimeout(task, millis);
        }

        /**
         * Cancels a timeout task.
         * 
         * @param taskId The task to cancel.
         */
        public static void clearTimeout(int taskId) {
            scheduler.clearTimeout(taskId);
        }

        /* ---------------- */
        /* Interval         */
        /* ---------------- */

        /**
         * Schedules a task to be run continuously every specified period.
         * 
         * @param  task The task to be run.
         * 
         * @return      a task id for use in {@link #clearInterval(int)}.
         */
        public static int setInterval(@NonNull Runnable task, long millis) {
            return scheduler.setInterval(task, millis);
        }

        /**
         * Cancels an interval task.
         * 
         * @param taskId The task to cancel.
         */
        public static void clearInterval(int taskId) {
            scheduler.clearInterval(taskId);
        }

        /* ---------------- */
        /* Utilities        */
        /* ---------------- */

        /**
         * Schedules a task to be run after the specified amount of time.
         * 
         * @param  task The task to be run.
         * 
         * @return      a task id for use in {@link #clearTimeout(int)}.
         */
        public static int setTimeout(@NonNull Runnable task, long duration, @NonNull TimeUnit unit) {
            return scheduler.setTimeout(task, duration, unit);
        }

        /**
         * Schedules a task to be run continuously every specified period.
         * 
         * @param  task The task to be run.
         * 
         * @return      a task id for use in {@link #clearInterval(int)}.
         */
        public static int setInterval(@NonNull Runnable task, long duration, @NonNull TimeUnit unit) {
            return scheduler.setInterval(task, duration, unit);
        }

    }

}
