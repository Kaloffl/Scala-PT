package kaloffl.spath

import kaloffl.spath.math.Color

trait RenderTarget {

  def width: Int
  def height: Int

  def setPixel(x: Int, y: Int, color: Color): Unit

  def commit: Unit
}