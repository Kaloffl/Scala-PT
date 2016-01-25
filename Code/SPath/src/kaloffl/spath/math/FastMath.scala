package kaloffl.spath.math

/**
 * This Object contains functions that implement slow math functions in a
 * faster but probably less accurate fashion.
 */
object FastMath {

  val halfPi = Math.PI / 2.0

  /**
   * Approximation of the asin function by use of the square root
   */
  def asin(n: Double): Double = {
    if (n < 0) {
      (Math.sqrt(n % 1 + 1) - 1) * halfPi
    } else {
      (1 - Math.sqrt(1 - n % 1)) * halfPi
    }
  }

  val ONEQTR_PI = Math.PI / 4.0
  val THRQTR_PI = 3.0 * Math.PI / 4.0

  def atan2(y: Double, x: Double): Double = {
    //http://pubs.opengroup.org/onlinepubs/009695399/functions/atan2.html
    //Volkan SALMA
    // https://gist.github.com/volkansalma/2972237
    val abs_y = Math.abs(y) + 1e-10f // kludge to prevent 0/0 condition
    if (x < 0.0) {
      val r = (x + abs_y) / (abs_y - x)
      val angle = THRQTR_PI + (0.1963f * r * r - 0.9817f) * r
      if (y < 0.0) return -angle // negate if in quad III or IV
      return angle
    }
    val r = (x - abs_y) / (x + abs_y)
    val angle = ONEQTR_PI + (0.1963f * r * r - 0.9817f) * r
    if (y < 0.0) return -angle // negate if in quad III or IV
    return angle
  }
}