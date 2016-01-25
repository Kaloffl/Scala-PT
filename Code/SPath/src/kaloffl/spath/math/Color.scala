package kaloffl.spath.math

import java.util.function.DoubleSupplier

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

  def unary_-(): Color = new Color(1 - r2, 1 - g2, 1 - b2)

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

  def pow(f: Float): Color = {
    new Color(
      Math.pow(r2, f).toFloat,
      Math.pow(g2, f).toFloat,
      Math.pow(b2, f).toFloat)
  }
  def pow(c: Color): Color = {
    new Color(
      Math.pow(r2, c.r2).toFloat,
      Math.pow(g2, c.g2).toFloat,
      Math.pow(b2, c.b2).toFloat)
  }

  def exp(): Color = {
    new Color(
      Math.exp(r2).toFloat,
      Math.exp(g2).toFloat,
      Math.exp(b2).toFloat)
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

  def toInt: Int = {
    val ia = 0xff << 24
    val ir = Math.min(0xff, r * 0xff).toInt << 16
    val ig = Math.min(0xff, g * 0xff).toInt << 8
    val ib = Math.min(0xff, b * 0xff).toInt
    return ia | ir | ig | ib
  }
}

object Color {

  val Black = Color(0, 0, 0)
  val White = Color(1, 1, 1)

  val Red = Color(1, 0, 0)
  val Green = Color(0, 1, 0)
  val Blue = Color(0, 0, 1)

  def apply(r: Float, g: Float, b: Float) = new Color(r * r, g * g, b * b)
  def apply(v: Vec3d) = new Color(v.x.toFloat, v.y.toFloat, v.z.toFloat)

  def unapply(c: Color) = Some((c.r2, c.g2, c.b2))

  def randomColor(random: Vec2d, brightness: Float): Color = {
    val hue = random.x.toFloat
    val saturation = (random.y * 2000 + 4000).toFloat / 10000f
    return Color.fromHsb(hue, saturation, brightness)
  }

  def fromHsb(hue: Float, saturation: Float, brightness: Float): Color = {
    if (saturation == 0) {
      Color(brightness, brightness, brightness)
    } else {
      val h = (hue - Math.floor(hue).toFloat) * 6.0f
      val f = h - Math.floor(h).toFloat
      val p = brightness * (1.0f - saturation)
      val q = brightness * (1.0f - saturation * f)
      val t = brightness * (1.0f - (saturation * (1.0f - f)))
      (h.toInt) match {
        case 0 ⇒
          Color(brightness, t, p)
        case 1 ⇒
          Color(q, brightness, p)
        case 2 ⇒
          Color(p, brightness, t)
        case 3 ⇒
          Color(p, q, brightness)
        case 4 ⇒
          Color(t, p, brightness)
        case 5 ⇒
          Color(brightness, p, q)
      }
    }
  }
}