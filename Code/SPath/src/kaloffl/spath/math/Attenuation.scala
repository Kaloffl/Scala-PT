package kaloffl.spath.math

object Attenuation {
  val none = new Attenuation(1, 0, 0)
  def linear(linear: Double) = new Attenuation(1, linear, 0)
  def quadratic(quadratic: Double) = new Attenuation(1, 0, quadratic)
  def radius(r: Double) = new Attenuation(1, 2.0 / r, 1.0 / (r * r))
}

class Attenuation(
    val constant: Double,
    val linear: Double,
    val quadratic: Double) {

  def apply(dist: Double): Float = {
    return 1.0f / (constant + linear * dist + quadratic * dist * dist).toFloat
  }
}