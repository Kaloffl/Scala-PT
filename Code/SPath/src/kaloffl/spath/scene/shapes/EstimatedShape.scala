package kaloffl.spath.scene.shapes

import kaloffl.spath.math.{Ray, Vec2d, Vec3d}

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

  override def getTextureCoordinate(point: Vec3d): Vec2d = {
    val normal = getNormal(point)
    return Vec2d(
      Math.acos(normal.x / Math.sqrt(1.0 - normal.z * normal.z)) / 2 / Math.PI,
      (normal.z + 1) / 2)
  }

  override def getIntersectionDepth(ray: Ray): Double = {
    var depthSum = 0.0
    var maxDepth = Double.PositiveInfinity
    var counter = 0
    while (counter < 200) {
      val pos = ray.atDistance(depthSum)
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
}