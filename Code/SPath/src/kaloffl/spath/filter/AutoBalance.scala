package kaloffl.spath.filter

import kaloffl.spath.RenderTarget
import kaloffl.spath.math.Color

class AutoBalance(override val target: RenderTarget) extends Filter {

  var minR = Float.MaxValue
  var minG = Float.MaxValue
  var minB = Float.MaxValue

  var maxR = 0f
  var maxG = 0f
  var maxB = 0f

  val colorBuffer = new Array[Color](width * height)

  override def setPixel(x: Int, y: Int, color: Color): Unit = {
    if (minR > color.r2) minR = color.r2
    if (minG > color.g2) minG = color.g2
    if (minB > color.b2) minB = color.b2
    if (maxR < color.r2) maxR = color.r2
    if (maxG < color.g2) maxG = color.g2
    if (maxB < color.b2) maxB = color.b2
    colorBuffer(x * height + y) = color
  }

  override def commit: Unit = {
    val diffR = maxR - minR
    val diffG = maxG - minG
    val diffB = maxB - minB
    for (x ← 0 until width; y ← 0 until height) {
      val original = colorBuffer(x * height + y)
      target.setPixel(x, y, new Color(
        (original.r2 - minR) / diffR,
        (original.g2 - minG) / diffG,
        (original.b2 - minB) / diffB))
    }
    minR = Float.MaxValue
    minG = Float.MaxValue
    minB = Float.MaxValue
    maxR = 0f
    maxG = 0f
    maxB = 0f
    target.commit
  }
}