package kaloffl.jobs

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.LockSupport

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
    // TODO I'm pretty sure that the pool won't stop while some jobs are
    // still unfinished. However the stopping criteria is that no worker 
    // is doing anything, so I'm not 100% convinced that it is correct.
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
      // TODO this setup isn't ideal: as long as there are jobs, the 
      // workers will poll the queue for work, even if none of the jobs 
      // can be executed.
      // What we need is a way to park the workers if they can't find 
      // executable jobs and reactivate them when jobs become executable.

      while (true) {
        val job = queue.poll
        if (null == job) {
          return null
        }
        if (!job.canExecute) {
          queue.add(job)
        } else {
          return job
        }
      }
      return null
    }
  }
}