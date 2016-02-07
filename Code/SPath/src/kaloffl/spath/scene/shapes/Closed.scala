package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d

trait Closed {
  
  def contains(point: Vec3d): Boolean
}