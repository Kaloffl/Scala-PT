package kaloffl.spath.filter

import kaloffl.spath.RenderTarget

trait Filter extends RenderTarget {
  
  def target: RenderTarget
  
  override def commit(): Unit = target.commit()
  override def height: Int = target.height
  override def width: Int = target.width
  
}