package au.com.codeka.warworlds.client.concurrency;

import static com.google.common.base.Preconditions.checkState;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * An enumeration of the thread types within War Worlds. Has some helper methods to ensure you run
 * on a particular thread.
 */
public enum Threads {
  /**
   * The main UI thread.
   */
  UI,

  /**
   * The OpenGL render thread. We assume there is only one of these in the whole process.
   */
  GL,

  /**
   * A special "class" of thread that actually represents a pool of background workers.
   */
  BACKGROUND;

  public static void checkOnThread(Threads thread) {
    // Note: We don't use Preconditions.checkState because we want a nice error message and don't
    // want to allocate the string for the message every time.
    if (!thread.isCurrentThread()) {
      throw new IllegalStateException("Unexpectedly not on " + thread);
    }
  }

  public static void checkNotOnThread(Threads thread) {
    if(thread.isCurrentThread()) {
      throw new IllegalStateException("Unexpectedly on " + thread);
    }
  }

  private boolean isInitialized;
  @Nullable private Handler handler;
  @Nullable private TaskQueue taskQueue;
  @Nullable private Thread thread;
  @Nullable private ThreadPool threadPool;

  public void setThread(@NonNull Thread thread, @NonNull TaskQueue taskQueue) {
    checkState(!isInitialized || this.taskQueue == taskQueue);
    this.thread = thread;
    this.taskQueue = taskQueue;
    this.isInitialized = true;
  }

  public void setThread(@NonNull Thread thread, @NonNull Handler handler) {
    checkState(!isInitialized);
    this.thread = thread;
    this.handler = handler;
    this.isInitialized = true;
  }

  /** Reset this thread, unassociate it from the current thread, handler, task queue combo. */
  public void resetThread() {
    thread = null;
    handler = null;
    taskQueue = null;
    isInitialized = false;
  }

  public void setThreadPool(@NonNull ThreadPool threadPool) {
    checkState(!isInitialized);
    this.threadPool = threadPool;
    this.isInitialized = true;
  }

  public boolean isCurrentThread() {
    checkState(isInitialized);

    if (thread != null) {
      return thread == Thread.currentThread();
    } else if (threadPool != null) {
      return threadPool.isThread(this);
    } else {
      throw new IllegalStateException("thread is null and threadPool is null");
    }
  }

  public void runTask(Runnable runnable) {
    if (handler != null) {
      handler.post(runnable);
    } else if (threadPool != null) {
      threadPool.runTask(runnable);
    } else if (taskQueue != null) {
      taskQueue.postTask(runnable);
    } else {
      throw new IllegalStateException("Cannot run task, no handler, taskQueue or threadPool!");
    }
  }
}
