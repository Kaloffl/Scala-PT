package kaloffl.jobs

class Worker(jobPool: JobPool) extends Runnable {

  var running = true

  override def run: Unit = {
    while (running) {
      val job = jobPool.pollJob
      if (null == job) {
        jobPool.park(this)
      } else {
        job.execute
      }
    }
  }
}