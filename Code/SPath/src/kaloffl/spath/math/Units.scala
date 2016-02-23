package kaloffl.spath.math

object Units {
  def km(length: Float) = length * 10e3f
  def m(length: Float) = length
  def dm(length: Float) = length * 10e-1f
  def cm(length: Float) = length * 10e-2f
  def mm(length: Float) = length * 10e-3f
}