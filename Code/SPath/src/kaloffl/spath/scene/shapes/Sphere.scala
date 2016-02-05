package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Ray
import java.util.function.DoubleSupplier
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.FastMath

/**
 * A sphere shape consisting of a location and a radius.
 */
class Sphere(val position: Vec3d, val radius: Float) extends Shape with Projectable with Bounded {

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
      FastMath.atan2(normal.x, normal.z) / (2 * Math.PI) + 0.5,
      FastMath.asin(-normal.y) / Math.PI + 0.5)
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

  override def getSolidAngle(point: Vec3d): Double = {
    val dsq = (point - position).lengthSq
    val asq = radius * radius / dsq
    val h = 1 - Math.sqrt(1 - asq)
    return (asq + h * h) / 2
  }
  
  override def createRandomRay(start: Vec3d, random: DoubleSupplier): Ray = {
    val direction = (position - start)
    val maxOffset = radius * Math.sqrt(1 - radius * radius / direction.lengthSq)
    val angle = random.getAsDouble * Math.PI * 2
    val offset = Math.sqrt(random.getAsDouble) * maxOffset
    val z = direction.normalize
    val x = z.ortho.normalize * Math.cos(angle) * offset
    val y = z.cross(x).normalize * Math.sin(angle) * offset
    val normal = (direction + x + y).normalize
    return new Ray(start, normal)
  }
}