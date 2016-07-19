package kaloffl.jobs

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.LockSupport

/**
 * This class offers similar functionality to the
 * {@link java.util.concurrent.CountDownLatch CountDownLatch} provided by the
 * JRE. However this Latch can not only count down but also up. The await
 * method will block the calling thread until the latch has reached the value 0.
 */
class CountLatch(initialValue: Int) {
  /**
   * Internal counter used to store and manipulate the counted value in a
   * thread-save way.
   */
  private val counter = new AtomicInteger(initialValue)

  /**
   * List of threads that have called the await method and need to be woken up
   * when the counter reaches 0.
   */
  private val waiting = new ConcurrentLinkedQueue[Thread]()

  /**
   * Method to manipulate the internally stored value. The given value is added
   * to the currently stored one in a thread-safe way. If the result is 0, all
   * waiting threads are woken up.
   */
  def addAndGet(i: Int): Int = {
    val number = counter.addAndGet(i)
    if (0 == number) {
      wakeThreads()
    }
    return number
  }

  /**
   * Adds one to the current value.
   */
  def countUp = addAndGet(1)

  /**
   * Subtracts one from the value.
   */
  def countDown = addAndGet(-1)

  /**
   * Returns the current value.
   */
  def getCount = counter.get

  /**
   * Calling this method will block the current thread until the internal value
   * of this object reaches 0. Then the thread is woken up and it will return to
   * wherever this method was called from.
   */
  def await(): Unit = {
    counter.synchronized {
      waiting.add(Thread.currentThread())
    }
    while (0 != counter.get) {
      LockSupport.park()
    }
    counter.synchronized {
      waiting.clear()
    }
  }

  /**
   * Wakes up all waiting threads. If the internal value is not 0 yet, they will
   * immediately go back to sleep.
   */
  def wakeThreads(): Unit = {
    val iter = waiting.iterator
    while (iter.hasNext) { LockSupport.unpark(iter.next) }
  }
}