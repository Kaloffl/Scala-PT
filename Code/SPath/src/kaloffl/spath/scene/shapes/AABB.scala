package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Ray
import java.util.function.DoubleSupplier
import kaloffl.spath.math.Vec2d

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

  def enclosing[T](objects: Array[_ <: T], enclose: T ⇒ AABB): AABB = {
    if (0 == objects.length) return new AABB(Vec3d.Origin, Vec3d.Origin)
    var min = enclose(objects(0)).min
    var max = enclose(objects(0)).max
    var i = 1
    while (i < objects.length) {
      val objBB = enclose(objects(i))
      min = min.min(objBB.min)
      max = max.max(objBB.max)
      i += 1
    }
    return new AABB(min, max)
  }
}

class AABB(val min: Vec3d, val max: Vec3d) extends Shape with Bounded {

  override def getBounds: AABB = this

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
    if (minDst == dist1.x) return Vec3d.Left
    if (minDst == dist2.x) return Vec3d.Right
    if (minDst == dist1.y) return Vec3d.Up
    if (minDst == dist2.y) return Vec3d.Down
    if (minDst == dist1.z) return Vec3d.Front
    if (minDst == dist2.z) return Vec3d.Back
    throw new RuntimeException(
      s"Could not determine AABB normal for point: $point. AABB bounds are max: $max, min: $min.")
  }

  override def getTextureCoordinate(point: Vec3d): Vec2d = {
    val dist1 = (point - max).abs
    val dist2 = (point - min).abs
    val minDst = Math.min(dist1.min, dist2.min)
    val t = (point - min) / (max - min)
    if (minDst == dist1.x) return Vec2d(t.y, t.z)
    if (minDst == dist2.x) return Vec2d(t.y, t.z)
    if (minDst == dist1.y) return Vec2d(t.x, t.z)
    if (minDst == dist2.y) return Vec2d(t.x, t.z)
    if (minDst == dist1.z) return Vec2d(t.x, t.y)
    if (minDst == dist2.z) return Vec2d(t.x, t.y)
    throw new RuntimeException(
      s"Could not determine Texture Coordinate for point: $point. AABB bounds are max: $max, min: $min.")
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

  /**
   * Returns a new AABB that exactly encloses this and the given AABB
   */
  def enclose(other: AABB): AABB = {
    return new AABB(min.min(other.min), max.max(other.max))
  }

  /**
   * Returns the overlapping space of two AABBs. If they don't overlap, an AABB
   * with no size positioned at the origin vector is returned. If performance
   * wasn't such an issue, this method would return an Option[AABB].
   */
  def overlap(other: AABB): AABB = {
    val x1 = Math.max(min.x, other.min.x)
    val y1 = Math.max(min.y, other.min.y)
    val z1 = Math.max(min.z, other.min.z)

    val x2 = Math.min(max.x, other.max.x)
    val y2 = Math.min(max.y, other.max.y)
    val z2 = Math.min(max.z, other.max.z)

    if (x1 < x2 && y1 < y2 && z1 < z2) {
      new AABB(Vec3d(x1, y2, z2), Vec3d(x2, y2, z2))
    } else {
      new AABB(Vec3d.Origin, Vec3d.Origin)
    }
  }

  def surfaceArea: Double = {
    val size = this.size
    return (size.x * (size.y + size.z) + size.y * size.z) * 2
  }

  def contains(v: Vec3d): Boolean = {
    return min.x <= v.x && v.x <= max.x &&
      min.y <= v.y && v.y <= max.y &&
      min.z <= v.z && v.z <= max.z
  }
}