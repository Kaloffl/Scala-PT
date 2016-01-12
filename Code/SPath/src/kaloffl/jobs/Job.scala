package kaloffl.jobs

/**
 * A Job is a piece of computational work that can be executed by the JobPool.
 * If it has to wait for something before it can do its work, it can use the
 * canExecute function to signal that it is not ready. As long as canExecute is
 * false, the JobPool will not start this Job and do others instead.
 */
trait Job {
  def canExecute: Boolean = true
  def execute: Unit
}