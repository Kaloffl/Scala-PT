package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Ray

trait EstimatedShape extends Shape {

  def estimateDepth(p: Vec3d): Double

  override def getNormal(point: Vec3d): Vec3d = {
    val xDir = Vec3d(0.00001, 0, 0)
    val yDir = Vec3d(0, 0.00001, 0)
    val zDir = Vec3d(0, 0, 0.00001)
    return Vec3d(
      estimateDepth(point + xDir) - estimateDepth(point - xDir),
      estimateDepth(point + yDir) - estimateDepth(point - yDir),
      estimateDepth(point + zDir) - estimateDepth(point - zDir)).normalize
  }

  override def getIntersectionDepth(ray: Ray): Double = {
    var depthSum = estimateDepth(ray.start + ray.normal * 0.002)
    if (depthSum < 0.0008) return Double.PositiveInfinity
    var counter = 0
    while (counter < 200) {
      val pos = ray.start + ray.normal * depthSum
      var depth = estimateDepth(pos)
      depthSum += depth

      if (depth < 0.001) {
        return depthSum
      }
      counter += 1
    }
    return Double.PositiveInfinity
  }
}