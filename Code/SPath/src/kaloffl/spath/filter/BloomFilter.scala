package kaloffl.spath.filter

import kaloffl.spath.RenderTarget
import kaloffl.spath.math.Color

class BloomFilter(override val target: RenderTarget, val fallFactor: Float, val power: Float) extends Filter {

  private val ExpFF = Math.exp(-fallFactor)

  val colorBuffer = new Array[Float](3 * width * height)

  override def setPixel(x: Int, y: Int, color: Color): Unit = {
    val rgb = Array(color.r2, color.g2, color.b2)
    for (c ← 0 until 3) {
      val cval = rgb(c)
      val range = Math.pow(cval, power).toInt
      if (range < 1) {
        colorBuffer((x * height + y) * 3 + c) += cval
      } else {
        val startX = Math.max(0, x - range)
        val startY = Math.max(0, y - range)
        val endX = Math.min(width - 1, x + range)
        val endY = Math.min(height - 1, y + range)
        for (rx ← startX to endX; ry ← startY to endY) {
          val dx = rx - x
          val dy = ry - y
          val dist = Math.sqrt(dx * dx + dy * dy)
          if (dist < range) {
            val index = (rx * height + ry) * 3 + c
            if (colorBuffer(index) < 1) {
              val color = (-Math.log(dist / range + ExpFF) / fallFactor * (Math.cos(dist) + 1) / 2).toFloat
              colorBuffer(index) += color
            }
          }
        }
      }
    }
  }

  override def commit(): Unit = {
    for (x ← 0 until width; y ← 0 until height) {
      val i = (x * height + y) * 3
      val color = new Color(
        colorBuffer(i),
        colorBuffer(i + 1),
        colorBuffer(i + 2))
      colorBuffer(i) = 0
      colorBuffer(i + 1) = 0
      colorBuffer(i + 2) = 0
      target.setPixel(x, y, color)
    }
    target.commit()
  }
}