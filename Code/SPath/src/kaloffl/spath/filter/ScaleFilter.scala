package kaloffl.spath.filter

import kaloffl.spath.RenderTarget
import kaloffl.spath.math.Color

class ScaleFilter(
    override val target: RenderTarget,
    val pixelWidth: Int, 
    val pixelHeight: Int) extends Filter {
  
  override def width: Int = target.width / pixelWidth
  override def height: Int = target.height / pixelHeight
  
  override def setPixel(x: Int, y: Int, color: Color): Unit = {
    val startX = x * pixelWidth
    val startY = y * pixelHeight
    val endX = startX + pixelWidth
    val endY = startY + pixelHeight
    
    for(x <- startX until endX; y <- startY until endY) {
      target.setPixel(x, y, color)
    }
  }
}