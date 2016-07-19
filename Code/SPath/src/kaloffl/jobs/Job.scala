package kaloffl.jobs

/**
 * A Job is a piece of computational work that can be executed by the JobPool.
 * If it has to wait for something before it can do its work, it can use the
 * canExecute function to signal that it is not ready. As long as canExecute is
 * false, the JobPool will skip this Job and execute others instead.
 */
trait Job {
  
  /**
   * Indicates to the JobPool weather this Job can be executed. If it returns
   * true, it will call the execute method, otherwise it will do something else
   * and check this method again later.
   */
  def canExecute: Boolean = true
  
  /**
   * This method is supposed to contain the computationally expensive code that
   * will be run in parallel by the job pool.
   */
  def execute(): Unit
}