package kaloffl.jobs

trait Job {

  def canExecute: Boolean
  def execute: Unit
}