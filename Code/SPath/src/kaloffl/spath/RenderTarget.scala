package kaloffl.spath

trait RenderTarget {

  def width: Int
  def height: Int
  
  def setPixel(x: Int, y: Int, color: Int)
  
  def update
}