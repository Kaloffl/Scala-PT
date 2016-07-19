package kaloffl.spath.math

/**
 * A 3 dimensional mathematical vector of double precision floating point
 * numbers. It implements the usual operations like addition and scaling but
 * also reflection, refraction and generation of random normal vectors centered
 * around the original vector.
 * These vectors are immutable and each operation creates a new instance with
 * the new values.
 */
case class Vec4d(x: Double, y: Double, z: Double, w: Double) {

  def +(v: Vec4d): Vec4d = Vec4d(x + v.x, y + v.y, z + v.z, w + v.w)
  def +(f: Double): Vec4d = Vec4d(x + f, y + f, z + f, w + f)

  def -(v: Vec4d): Vec4d = Vec4d(x - v.x, y - v.y, z - v.z, w - v.w)
  def -(f: Double): Vec4d = Vec4d(x - f, y - f, z - f, w - f)

  def *(v: Vec4d): Vec4d = Vec4d(x * v.x, y * v.y, z * v.z, w * v.w)
  def *(f: Double): Vec4d = Vec4d(x * f, y * f, z * f, w * f)

  def /(v: Vec4d): Vec4d = Vec4d(x / v.x, y / v.y, z / v.z, w / v.w)
  def /(f: Double): Vec4d = Vec4d(x / f, y / f, z / f, w / f)

  def unary_-(): Vec4d = Vec4d(-x, -y, -z, -w)

  def inverse = Vec4d(1 / x, 1 / y, 1 / z, 1 / w)

  def dot(v: Vec4d): Double = x * v.x + y * v.y + z * v.z + w * v.w

  def sum(): Double = x + y + z + w
  def lengthSq: Double = x * x + y * y + z * z + w * w
  def length: Double = Math.sqrt(x * x + y * y + z * z + w * w)

  def min = Math.min(Math.min(x, y), Math.min(z, w))
  def max = Math.max(Math.max(x, y), Math.max(z, w))
  def abs = Vec4d(Math.abs(x), Math.abs(y), Math.abs(z), Math.abs(w))

  def normalize: Vec4d = {
    val len = Math.sqrt(x * x + y * y + z * z + w * w)
    Vec4d(x / len, y / len, z / len, w / len)
  }
}
