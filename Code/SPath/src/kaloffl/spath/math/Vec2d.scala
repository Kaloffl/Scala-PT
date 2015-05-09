package kaloffl.spath.math

import java.util.function.DoubleSupplier

case class Vec2d(val x: Double, val y: Double) {

  def +(v: Vec2d): Vec2d = Vec2d(x + v.x, y + v.y)
  def +(f: Double): Vec2d = Vec2d(x + f, y + f)

  def -(v: Vec2d): Vec2d = Vec2d(x - v.x, y - v.y)
  def -(f: Double): Vec2d = Vec2d(x - f, y - f)

  def *(v: Vec2d): Vec2d = Vec2d(x * v.x, y * v.y)
  def *(f: Double): Vec2d = Vec2d(x * f, y * f)

  def /(v: Vec2d): Vec2d = Vec2d(x / v.x, y / v.y)
  def /(f: Double): Vec2d = Vec2d(x / f, y / f)

  def unary_-(): Vec2d = Vec2d(-x, -y)

  def dot(v: Vec2d): Double = x * v.x + y * v.y

  def sum(): Double = x + y
  def lengthSq: Double = x * x + y * y
  def length: Double = Math.sqrt(x * x + y * y)

  def min = Math.min(x, y)
  def max = Math.max(x, y)
  def abs = Vec2d(Math.abs(x), Math.abs(y))

  def pow(v: Vec2d): Vec2d = {
    Vec2d(Math.pow(x, v.x), Math.pow(y, v.y))
  }
  def pow(f: Double): Vec2d = {
    Vec2d(Math.pow(x, f), Math.pow(y, f))
  }

  def reflect(v: Vec2d): Vec2d = {
    val a = (x * v.x + y * v.y) * 2.0
    Vec2d(
      x - (v.x * a),
      y - (v.y * a))
  }

  def normalize: Vec2d = {
    val len = Math.sqrt(x * x + y * y)
    Vec2d(x / len, y / len)
  }
  
  def angle: Double = Math.atan2(y, x)
}

/**
 * Useful standard vectors are predefined here.
 */
object Vec2d {
  val ORIGIN: Vec2d = Vec2d(0, 0)
  val UNIT: Vec2d = Vec2d(1, 1)
  val NEGATIVE: Vec2d = Vec2d(-1, -1)

  val NORMAL = UNIT.normalize

  val LEFT: Vec2d = Vec2d(1, 0)
  val RIGHT: Vec2d = Vec2d(-1, 0)
  val UP: Vec2d = Vec2d(0, 1)
  val DOWN: Vec2d = Vec2d(0, -1)

  def apply(): Vec2d = ORIGIN
  def apply(d: Double): Vec2d = new Vec2d(d, d)

  def randomNormal(random: DoubleSupplier): Vec2d = {
    val angle = random.getAsDouble * 2.0 * Math.PI
    return Vec2d(Math.cos(angle), Math.sin(angle))
  }
  
  def fromXAndAngle(x: Double, angle: Double): Vec2d = {
    return Vec2d(x, x / Math.sin(Math.PI / 2 - angle) * Math.sin(angle));
  }

  def fromYAndAngle(y: Double, angle: Double): Vec2d = {
    return Vec2d(y / Math.sin(angle) * Math.sin(Math.PI / 2 - angle), y);
  }

  def fromLengthAndAngle(length: Double, angle: Double): Vec2d = {
    return Vec2d(Math.cos(angle) * length, Math.sin(angle) * length);
  }
}