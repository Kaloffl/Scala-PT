package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import java.awt.image.BufferedImage

trait Texture {
  def width: Int
  def height: Int

  def apply(x: Float, y: Float): Color
}

class ArrayTexture(override val width: Int,
                   override val height: Int,
                   data: Array[Color]) extends Texture {

  override def apply(x: Float, y: Float): Color = {
    data((y % 1 * height).toInt + (x % 1 * width).toInt * height)
  }
}

class LazyTexture(image: BufferedImage) extends Texture {
  override def width = image.getWidth
  override def height = image.getHeight

  override def apply(x: Float, y: Float): Color = {
    val rgb = image.getRGB((x % 1 * width).toInt, (y % 1 * height).toInt)
    val r = ((rgb >> 16) & 0xff) / 255f
    val g = ((rgb >> 8) & 0xff) / 255f
    val b = (rgb & 0xff) / 255f
    return Color(r, g, b)
  }
}