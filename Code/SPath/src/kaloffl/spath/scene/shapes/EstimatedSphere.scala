package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d

class EstimatedSphere(position: Vec3d, radius: Double) extends EstimatedShape {

//  override def getNormal(point: Vec3d): Vec3d = (point - position).normalize
  
  override def estimateDepth(point: Vec3d): Double = {
    return (position - point).length - radius
  }
  
  override def enclosingAABB: AABB = {
    return AABB(position, Vec3d(radius * 2))
  }
}