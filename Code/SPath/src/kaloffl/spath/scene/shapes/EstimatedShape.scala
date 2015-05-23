package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Ray
import java.util.function.DoubleSupplier

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
    var depthSum = 0.0
    var maxDepth = Double.PositiveInfinity
    val aabb = enclosingAABB
    if (!aabb.contains(ray.start)) {
      depthSum = aabb.getIntersectionDepth(ray)
      if (java.lang.Double.isInfinite(depthSum)) return Double.PositiveInfinity
      val innerRay = new Ray(ray.start + ray.normal * (depthSum + 0.01), ray.normal)
      maxDepth = aabb.getIntersectionDepth(innerRay)
    } else {
      maxDepth = aabb.getIntersectionDepth(ray)
      depthSum = estimateDepth(ray.start + ray.normal * 0.002)
      if (depthSum < 0.0008) return Double.PositiveInfinity
    }
    var counter = 0
    while (counter < 200) {
      val pos = ray.start + ray.normal * depthSum
      val depth = estimateDepth(pos)
      depthSum += depth

      if (depth < 0.001) {
        return depthSum
      }
      if (depthSum >= maxDepth) {
        return Double.PositiveInfinity
      }
      counter += 1
    }
    return Double.PositiveInfinity
  }

  override def randomSurfacePoint(rng: DoubleSupplier): Vec3d = enclosingAABB.randomSurfacePoint(rng)
}