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
}