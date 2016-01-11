package kaloffl.jobs

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.LockSupport

class CountLatch(initialValue: Int) {
  val counter = new AtomicInteger(initialValue)

  var waiting = Seq[Thread]()

  def addAndGet(i: Int): Int = {
    val number = counter.addAndGet(i)
    if (0 == number) {
      wakeThreads
    }
    return number
  }

  def countUp: Int = addAndGet(1)
  def countDown: Int = addAndGet(-1)

  def getCount = counter.get

  def await: Unit = {
    // TODO I think this list concatenation is not save and should be synchronized
    waiting = Thread.currentThread() +: waiting
    while (0 != counter.get) {
      LockSupport.park
    }
    waiting = Seq[Thread]()
  }

  def wakeThreads: Unit = {
    waiting.foreach { LockSupport.unpark(_) }
  }
}