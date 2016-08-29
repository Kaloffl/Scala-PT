package kaloffl.jobs

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.LockSupport

/**
 * The JobPool is supposed to be an alternative to the default Java Fork-Join
 * pool with some relaxed requirements. Instead of pausing execution somewhere
 * in the middle of a method when calling join, users of this pool will have to
 * split the method in two and use the canExecute method to make the second part
 * wait on the first.<br>
 * New Jobs are submitted to the pool via the submit method. Every submitted
 * job is potentially executed immediately as long as canExecute is true.<br>
 * When the method execute is called, the caller will act as another worker
 * until all jobs are done, at which point it will return from the execute
 * method.
 */
class JobPool(numThreads: Int = Runtime.getRuntime.availableProcessors) {
  val jobQueue = new ConcurrentLinkedQueue[Job]
  val waitingWorkers = new ConcurrentLinkedQueue[Worker]
  private val numberOfWorkers = numThreads - 1
  private val workers = Array.tabulate(numberOfWorkers) { _ ⇒ new Worker(this) }
  private val mainWorker = new Worker(this)
  private val threads = Array.tabulate(numberOfWorkers) { i ⇒ new Thread(workers(i)) }
  private val counter = new CountLatch(numberOfWorkers)
  threads.foreach { _.start }

  /**
   * Adds a new job to the pool that will be picked up and executed by a worker
   * at some point.
   */
  def submit(job: Job): Unit = {
    jobQueue.add(job)
    val worker = waitingWorkers.poll
    if (null != worker) {
      worker.running = true
      unpark(threads(workers.indexOf(worker)))
    }
  }

  /**
   * Parks the calling thread and decreases the CountLatch that keeps track of
   * the active workers.
   */
  private def park(): Unit = {
    counter.countDown
    LockSupport.park()
  }

  /**
   * Wakes up the given thread and increases the CountLatch that keeps track of
   * the active workers.
   */
  private def unpark(t: Thread): Unit = {
    LockSupport.unpark(t)
    counter.countUp
  }

  /**
   * The caller of this method will also act as a worker in this pool until all
   * Jobs were executed, only then will it return from this method.
   */
  def execute(): Unit = {
    // TODO I'm pretty sure that the pool won't stop while some jobs are
    // still unfinished. However the stopping criteria is that no worker 
    // is doing anything, so I'm not 100% convinced that it is correct.
    mainWorker.running = true
    mainWorker.run()
    counter.await()
  }

  /**
   * Parks the given worker and the calling thread. This method assumes that
   * the worker and calling thread belong together.
   */
  def park(worker: Worker) {
    worker.running = false
    if (worker != mainWorker) {
      waitingWorkers.add(worker)
      park()
    }
  }

  /**
   * Searches the submitted jobs for executable ones and returns the first it
   * finds.
   */
  def pollJob(): Job = {
    // TODO this setup isn't ideal: as long as there are jobs, the 
    // workers will poll the queue for work, even if none of the jobs 
    // can be executed.
    // What we need is a way to park the workers if they can't find 
    // executable jobs and reactivate them when jobs become executable.

    while (true) {
      val job = jobQueue.poll
      if (null == job) {
        return null
      }
      if (!job.canExecute) {
        jobQueue.add(job)
      } else {
        return job
      }
    }
    return null
  }
}