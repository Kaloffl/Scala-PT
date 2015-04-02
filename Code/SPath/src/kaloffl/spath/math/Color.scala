package kaloffl.spath.math

class Color(val r: Float, val g: Float, val b: Float) {

  def +(f: Float): Color = Color(r + f, g + f, b + f)
  def +(c: Color): Color = Color(r + c.r, g + c.g, b + c.b)

  def -(f: Float): Color = Color(r - f, g - f, b - f)
  def -(c: Color): Color = Color(r - c.r, g - c.g, b - c.b)

  def *(f: Float): Color = Color(r * f, g * f, b * f)
  def *(c: Color): Color = Color(r * c.r, g * c.g, b * c.b)

  def /(f: Float): Color = Color(r / f, g / f, b / f)
  def /(c: Color): Color = Color(r / c.r, g / c.g, b / c.b)
}

object Color {

  val BLACK = Color(0, 0, 0)
  val WHITE = Color(1, 1, 1)

  val RED = Color(1, 0, 0)
  val GREEN = Color(0, 1, 0)
  val BLUE = Color(0, 0, 1)

  def apply(r: Float, g: Float, b: Float) = new Color(r, g, b)
  def apply(v: Vec3d) = new Color(v.x.toFloat, v.y.toFloat, v.z.toFloat)
}