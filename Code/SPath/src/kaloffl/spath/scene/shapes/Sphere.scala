package kaloffl.spath.scene.shapes

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Ray, Vec2d, Vec3d}

/**
 * A sphere shape consisting of a location and a radius.
 */
class Sphere(val position: Vec3d, val radius: Float) extends Shape with Bounded with Closed with Emitter {

  val radiusSq = radius * radius

  override def getNormal(point: Vec3d): Vec3d = {
    Vec3d(
      (point.x - position.x) / radius,
      (point.y - position.y) / radius,
      (point.z - position.z) / radius)
  }

  override def getTextureCoordinate(point: Vec3d): Vec2d = {
    val normal = getNormal(point)
    return Vec2d(
      Math.atan2(normal.x, normal.z) / (2 * Math.PI) + 0.5,
      Math.asin(-normal.y) / Math.PI + 0.5)
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

  override def getBounds: AABB = {
    AABB(position, Vec3d(radius * 2))
  }

  override def contains(point: Vec3d): Boolean = {
    (position - point).lengthSq < radiusSq
  }
  
  override def getRandomPointOfSurface(random: DoubleSupplier): Vec3d = {
    return Vec3d.randomNormal(Vec2d.random(random)) * radius + position
  }
}