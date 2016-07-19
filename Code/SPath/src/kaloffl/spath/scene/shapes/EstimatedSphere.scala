package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d

class EstimatedSphere(position: Vec3d, radius: Double) extends EstimatedShape with Bounded {

  override def estimateDepth(point: Vec3d): Double = (position - point).length - radius

  override def getBounds: AABB = AABB(position, Vec3d(radius * 2))
}