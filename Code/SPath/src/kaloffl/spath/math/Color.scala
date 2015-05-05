package kaloffl.spath.math

class Color(val r2: Float, val g2: Float, val b2: Float) {

  def r: Float = Math.sqrt(r2).toFloat
  def g: Float = Math.sqrt(g2).toFloat
  def b: Float = Math.sqrt(b2).toFloat

  def +(f: Float): Color = new Color(r2 + f, g2 + f, b2 + f)
  def +(c: Color): Color = new Color(r2 + c.r2, g2 + c.g2, b2 + c.b2)

  def -(f: Float): Color = new Color(r2 - f, g2 - f, b2 - f)
  def -(c: Color): Color = new Color(r2 - c.r2, g2 - c.g2, b2 - c.b2)

  def *(f: Float): Color = new Color(r2 * f, g2 * f, b2 * f)
  def *(c: Color): Color = new Color(r2 * c.r2, g2 * c.g2, b2 * c.b2)

  def /(f: Float): Color = new Color(r2 / f, g2 / f, b2 / f)
  def /(c: Color): Color = new Color(r2 / c.r2, g2 / c.g2, b2 / c.b2)

  def clamp(min: Color, max: Color): Color = {
    new Color(
      Math.min(Math.max(min.r2, r2), max.r2),
      Math.min(Math.max(min.g2, g2), max.g2),
      Math.min(Math.max(min.b2, b2), max.b2))
  }

  def max(c: Color): Color = {
    new Color(
      Math.max(c.r2, r2),
      Math.max(c.g2, g2),
      Math.max(c.b2, b2))
  }

  def min(c: Color): Color = {
    new Color(
      Math.min(r2, c.r2),
      Math.min(g2, c.g2),
      Math.min(b2, c.b2))
  }

  def mix(other: Color, factor: Float): Color = {
    return this * (1 - factor) + other * factor
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case Color(r2, g2, b2) ⇒ this.r2 == r2 && this.g2 == g2 && this.b2 == b2
      case _                 ⇒ false
    }
  }

  override def toString(): String = {
    return s"Color(r2: $r2, g2: $g2, b2: $b2)"
  }
}

object Color {

  val BLACK = Color(0, 0, 0)
  val WHITE = Color(1, 1, 1)

  val RED = Color(1, 0, 0)
  val GREEN = Color(0, 1, 0)
  val BLUE = Color(0, 0, 1)

  def apply(r: Float, g: Float, b: Float) = new Color(r * r, g * g, b * b)
  def apply(v: Vec3d) = new Color(v.x.toFloat, v.y.toFloat, v.z.toFloat)

  def unapply(c: Color) = Some((c.r2, c.g2, c.b2))
}