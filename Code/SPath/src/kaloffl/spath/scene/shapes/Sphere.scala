package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3f
import kaloffl.spath.tracing.Ray
import kaloffl.spath.math.Vec2f

class Sphere(val position: Vec3f, val radius: Float) extends Shape {

  val radiusSq = radius * radius

  def getNormal(point: Vec3f): Vec3f = {
    Vec3f(
      (point.x - position.x) / radius,
      (point.y - position.y) / radius,
      (point.z - position.z) / radius)
  }

  def getIntersectionDepth(ray: Ray): Float = {
    val start = ray.start
    val dx = position.x - start.x
    val dy = position.y - start.y
    val dz = position.z - start.z
    val normal = ray.normal
    val b = dx * normal.x + dy * normal.y + dz * normal.z
    val c = (dx * dx + dy * dy + dz * dz) - radiusSq
    val disc = b * b - c

    if (disc < 0.0f) return Float.PositiveInfinity

    val depth = b - Math.sqrt(disc).toFloat
    if (depth > 0.0001f) return depth

    return Float.PositiveInfinity
  }
}