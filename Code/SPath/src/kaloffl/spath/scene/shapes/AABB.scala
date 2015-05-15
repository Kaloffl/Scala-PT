package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Ray
import java.util.function.DoubleSupplier

/**
 * AABB stands for Axis Aligned Bounding Box and is a very simple and
 * computationally fast shape to test for intersection. However as the name
 * suggests, it is not possible to rotate this shape since the sides are aligned
 * along the three axis.
 */
object AABB {
  def apply(center: Vec3d, size: Vec3d): AABB = {
    new AABB(center - size / 2, center + size / 2)
  }
}

class AABB(val min: Vec3d, val max: Vec3d) extends Shape {

  def size: Vec3d = {
    max - min
  }
  def center: Vec3d = {
    Vec3d(
      (min.x + max.x) / 2,
      (min.y + max.y) / 2,
      (min.z + max.z) / 2)
  }

  override def getNormal(point: Vec3d): Vec3d = {
    val dist1 = (point - max).abs
    val dist2 = (point - min).abs
    val minDst = Math.min(dist1.min, dist2.min)
    if (minDst == dist1.x) return Vec3d.LEFT;
    if (minDst == dist2.x) return Vec3d.RIGHT;
    if (minDst == dist1.y) return Vec3d.UP;
    if (minDst == dist2.y) return Vec3d.DOWN;
    if (minDst == dist1.z) return Vec3d.FRONT;
    if (minDst == dist2.z) return Vec3d.BACK;
    throw new RuntimeException(
      "Could not determine AABB normal for point: " + point + ". AABB bounds are max: " + max + ", min: " + min + ".")
  }

  override def getIntersectionDepth(ray: Ray): Double = {
    val tx1 = (min.x - ray.start.x) / ray.normal.x
    val tx2 = (max.x - ray.start.x) / ray.normal.x

    val ty1 = (min.y - ray.start.y) / ray.normal.y
    val ty2 = (max.y - ray.start.y) / ray.normal.y

    val tz1 = (min.z - ray.start.z) / ray.normal.z
    val tz2 = (max.z - ray.start.z) / ray.normal.z

    var tmin = Math.max(Math.max(
      Math.min(tx1, tx2),
      Math.min(ty1, ty2)),
      Math.min(tz1, tz2))
    var tmax = Math.min(Math.min(
      Math.max(tx1, tx2),
      Math.max(ty1, ty2)),
      Math.max(tz1, tz2))

    if (tmax < tmin) return Double.PositiveInfinity
    if (tmin < 0.0001) {
      if (tmax < 0.0001) return Double.PositiveInfinity
      return tmax
    }
    return tmin
  }

  def enclose(other: AABB): AABB = {
    val sharedMin = Vec3d(
      Math.min(min.x, other.min.x),
      Math.min(min.y, other.min.y),
      Math.min(min.z, other.min.z))
    val sharedMax = Vec3d(
      Math.max(max.x, other.max.x),
      Math.max(max.y, other.max.y),
      Math.max(max.z, other.max.z))
    return new AABB(sharedMin, sharedMax)
  }

  def surfaceArea: Double = {
    val size = this.size
    return (size.x * (size.y + size.z) + size.y * size.z) * 2
  }

  def contains(v: Vec3d): Boolean = {
    return min.x <= v.x && v.x <= max.x && min.y <= v.y && v.y <= max.y && min.z <= v.z && v.z <= max.z
  }

  override def enclosingAABB: AABB = this
}