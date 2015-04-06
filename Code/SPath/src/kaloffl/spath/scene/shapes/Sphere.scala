package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Ray

/**
 * A sphere shape consisting of a location and a radius.
 */
class Sphere(val position: Vec3d, val radius: Float) extends Shape {

  val radiusSq = radius * radius

  override def getNormal(point: Vec3d): Vec3d = {
    Vec3d(
      (point.x - position.x) / radius,
      (point.y - position.y) / radius,
      (point.z - position.z) / radius)
  }

  override def getRandomInnerPoint(random: () ⇒ Double): Vec3d = {
    val dir =
      if (random.apply >= 0.5f) {
        Vec3d.UP
      } else {
        Vec3d.DOWN
      }.randomHemisphere(random)
    return dir * (random.apply * radius)
  }

  override def getIntersectionDepth(ray: Ray): Double = {
    val start = ray.start
    val dx = position.x - start.x
    val dy = position.y - start.y
    val dz = position.z - start.z
    val normal = ray.normal
    val b = dx * normal.x + dy * normal.y + dz * normal.z
    val c = (dx * dx + dy * dy + dz * dz) - radiusSq
    val disc = b * b - c

    if (disc < 0.0f) return Float.PositiveInfinity

    val sqrt = Math.sqrt(disc)
    val depth = b - sqrt
    if (depth > 0.0001) return depth
    
    val depth2 = b + sqrt
    if (depth2 > 0.0001) return depth2
    
    return Float.PositiveInfinity
  }

  override def enclosingAABB: AABB = {
    AABB(position, Vec3d(radius * 2))
  }
}