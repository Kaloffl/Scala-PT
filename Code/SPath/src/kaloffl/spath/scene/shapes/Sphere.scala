package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Ray
import java.util.function.DoubleSupplier

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

  override def getRandomInnerPoint(random: DoubleSupplier): Vec3d = {
    var point: Vec3d = null
    do {
      point = Vec3d(
        (random.getAsDouble * 2.0 - 1.0),
        (random.getAsDouble * 2.0 - 1.0),
        (random.getAsDouble * 2.0 - 1.0))
    } while (point.length > 1)
    return point * radius + position
    //    val w = random()
    //    val r = w * (2.0 - w) * radius
    //    val theta = 2.0 * Math.PI * random()
    //    val phi = Math.acos(2.0 * random() - 1.0)
    //    return Vec3d(
    //      position.x + (r * Math.sin(phi) * Math.cos(theta)),
    //      position.y + (r * Math.sin(phi) * Math.sin(theta)),
    //      position.z + (r * Math.cos(phi)))
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

  override def surfaceArea: Double = radiusSq * Math.PI * 4

  override def enclosingAABB: AABB = {
    AABB(position, Vec3d(radius * 2))
  }
}