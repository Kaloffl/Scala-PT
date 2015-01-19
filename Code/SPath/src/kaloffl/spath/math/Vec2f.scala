package kaloffl.spath.math

case class Vec2f(x: Float, y: Float) {

  def +(v: Vec2f): Vec2f = Vec2f(x + v.x, y + v.y)
  def +(f: Float): Vec2f = Vec2f(x + f, y + f)

  def -(v: Vec2f): Vec2f = Vec2f(x - v.x, y - v.y)
  def -(f: Float): Vec2f = Vec2f(x - f, y - f)

  def *(v: Vec2f): Vec2f = Vec2f(x * v.x, y * v.y)
  def *(f: Float): Vec2f = Vec2f(x * f, y * f)

  def /(v: Vec2f): Vec2f = Vec2f(x / v.x, y / v.y)
  def /(f: Float): Vec2f = Vec2f(x / f, y / f)

  def unary_- : Vec2f = Vec2f(-x, -y)

  def sum: Float = x + y
  def lengthSq: Float = x * x + y * y
  def length: Float = Math.sqrt(x * x + y * y).toFloat
  def angle: Float = Math.atan2(y, x).toFloat

  def normalize: Vec2f = {
    val length = Math.sqrt(x * x + y * y).toFloat
    Vec2f(x / length, y / length)
  }
}

object Vec2f {
  val ORIGIN = Vec2f(0, 0)
  val UNIT = Vec2f(1, 1)
  val NORMAL = UNIT.normalize
  val INFINITE = Vec2f(Float.PositiveInfinity, Float.PositiveInfinity)
}