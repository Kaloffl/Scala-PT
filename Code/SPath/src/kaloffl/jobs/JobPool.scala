package kaloffl.jobs

import java.util.LinkedList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.locks.LockSupport
import java.util.concurrent.ConcurrentLinkedQueue

class JobPool {
  val jobs = new JobQueue
  val waitingWorkers = new ConcurrentLinkedQueue[Worker]
  private val numberOfWorkers = Runtime.getRuntime.availableProcessors - 1
  private val workers = Array.tabulate(numberOfWorkers) { _ ⇒ new Worker(this) }
  private val mainWorker = new Worker(this)
  private val threads = Array.tabulate(numberOfWorkers) { i ⇒ new Thread(workers(i)) }
  private val counter = new CountLatch(numberOfWorkers)
  threads.foreach { _.start }

  def submit(job: Job): Unit = {
    jobs.queue.add(job)
    val worker = waitingWorkers.poll
    if (null != worker) {
      worker.running = true
      unpark(threads(workers.indexOf(worker)))
    }
  }

  private def park() = {
    counter.countDown
    LockSupport.park
  }

  private def unpark(t: Thread) = {
    LockSupport.unpark(t)
    counter.countUp
  }

  def execute: Unit = {
    mainWorker.running = true
    mainWorker.run
    counter.await
  }

  def park(worker: Worker) {
    worker.running = false
    if (worker != mainWorker) {
      waitingWorkers.add(worker)
      park
    }
  }

  class JobQueue {
    val queue = new ConcurrentLinkedQueue[Job]

    def poll: Job = {
      queue.poll
    }
  }
}