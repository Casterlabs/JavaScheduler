package co.casterlabs.javascheduler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * This class is accessible as Scheduler.Static.
 */
class StaticSchedulerImpl implements Scheduler {
    private int counter = 1;

    private List<Task> timeoutQueue = new LinkedList<>();
    private Map<Integer, IntervalTask> intervals = new HashMap<>();

    private Thread timeoutProcessThread = new Thread(this::__processTimeouts);

    public StaticSchedulerImpl() {
        this.timeoutProcessThread.setDaemon(true);
        this.timeoutProcessThread.setName("JavaScheduler Timeout Processing Thread");
        this.timeoutProcessThread.start();
    }

    @SneakyThrows
    private void __processTimeouts() {
        while (true) {
            long nextTaskIn = this.__getNextTaskTime() - System.currentTimeMillis();

            if (nextTaskIn <= 0) {
                Task t = this.timeoutQueue.remove(0);

                t.run();

                // Now execute the top of the loop again, do NOT sleep.
                continue;
            }

            synchronized (this.timeoutQueue) {
                // Either wait for the next task or wait for us to get woken up by the call.
                this.timeoutQueue.wait(nextTaskIn);
            }
        }
    }

    /**
     * Only call from __processTimeouts!!
     */
    private long __getNextTaskTime() {
        if (this.timeoutQueue.isEmpty()) {
            return Long.MAX_VALUE;
        }

        Task t = this.timeoutQueue.get(0);

        return t.executeAt;
    }

    @Override
    public int setTimeout(@NonNull Runnable task, long millis) {
        final int id = this.counter++;

        Task t = new Task(id, System.currentTimeMillis() + millis, task);

        this.timeoutQueue.add(t);
        this.timeoutQueue.sort((t1, t2) -> t1.executeAt > t2.executeAt ? 1 : -1); // Sort.

        synchronized (this.timeoutQueue) {
            this.timeoutQueue.notifyAll();
        }

        return id;
    }

    @Override
    public void clearTimeout(int taskId) {
        Iterator<Task> it = this.timeoutQueue.iterator();

        while (it.hasNext()) {
            Task task = it.next();

            if (task.id == taskId) {
                it.remove();
                break;
            }
        }
    }

    @Override
    public int setInterval(@NonNull Runnable task, long millis) {
        final int id = this.counter++;

        IntervalTask it = new IntervalTask(id, millis, task);

        this.intervals.put(id, it);
        it.start();

        return id;
    }

    @Override
    public void clearInterval(int taskId) {
        IntervalTask thread = this.intervals.remove(taskId);

        if (thread != null) {
            thread.shouldRun = false;
            thread.interrupt();
        }
    }

    static boolean execute(Runnable task) {
        try {
            task.run();
            return true;
        } catch (Throwable t) {
            System.err.println("[JavaScheduler] A task produced an error:");
            t.printStackTrace();
            return false;
        }
    }

}

@AllArgsConstructor
class Task {
    public int id;
    public long executeAt;
    public Runnable task;

    public void run() {
        Thread executionThread = new Thread(() -> {
            // We discard the success result.
            StaticSchedulerImpl.execute(this.task);
        });

        executionThread.setDaemon(true);
        executionThread.setName("JavaScheduler: Interval Thread #" + this.id);
        executionThread.start();
    }

}

class IntervalTask extends Thread {
    public int id;
    public long millis;
    public Runnable task;

    public boolean shouldRun = true;

    public IntervalTask(int id, long millis, Runnable task) {
        this.id = id;
        this.millis = millis;
        this.task = task;

        this.setDaemon(true);
        this.setName("JavaScheduler: Interval Thread #" + this.id);
    }

    @Override
    public void run() {
        while (this.shouldRun) {
            try {
                Thread.sleep(this.millis);
            } catch (InterruptedException e) {
                Thread.interrupted(); // Clear.
                return;
            }

            boolean success = StaticSchedulerImpl.execute(this.task);

            if (!success) {
                Scheduler.Static.clearTimeout(this.id);
                return;
            }
        }
    }

}
