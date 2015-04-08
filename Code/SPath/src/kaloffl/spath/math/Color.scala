package kaloffl.spath.math

class Color(val r2: Float, val g2: Float, val b2: Float) {

  def r: Float = Math.sqrt(r2).toFloat
  def g: Float = Math.sqrt(g2).toFloat
  def b: Float = Math.sqrt(b2).toFloat

  def +(f: Float): Color = {
    val f2 = f * f
    new Color(r2 + f2, g2 + f2, b2 + f2)
  }
  def +(c: Color): Color = new Color(r2 + c.r2, g2 + c.g2, b2 + c.b2)

  def -(f: Float): Color = {
    val f2 = f * f
    new Color(r2 - f2, g2 - f2, b2 - f2)
  }
  def -(c: Color): Color = new Color(r2 - c.r2, g2 - c.g2, b2 - c.b2)

  def *(f: Float): Color = {
    val f2 = f * f
    new Color(r2 * f2, g2 * f2, b2 * f2)
  }
  def *(c: Color): Color = new Color(r2 * c.r2, g2 * c.g2, b2 * c.b2)

  def /(f: Float): Color = {
    val f2 = f * f
    new Color(r2 / f2, g2 / f2, b2 / f2)
  }
  def /(c: Color): Color = new Color(r2 / c.r2, g2 / c.g2, b2 / c.b2)

  override def equals(obj: Any): Boolean = {
    obj match {
      case Color(r2, g2, b2) ⇒ this.r2 == r2 && this.g2 == g2 && this.b2 == b2
      case _                 ⇒ false
    }
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