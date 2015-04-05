package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Ray

/**
 * AABB stands for Axis Aligned Bounding Box and is a very simple and
 * computationally fast shape to test for intersection. However as the name
 * suggests, it is not possible to rotate this shape since the sides are aligned
 * along the three axis.
 */
class AABB(centerArg: Vec3d, sizeArg: Vec3d) extends Shape {

  val min = centerArg - sizeArg / 2
  val max = centerArg + sizeArg / 2

  def size: Vec3d = max - min
  def center: Vec3d = (min + max) / 2
  
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

  override def getRandomInnerPoint(random: () ⇒ Double): Vec3d = {
    return Vec3d(
      min.x + (size.x) * random(),
      min.y + (size.y) * random(),
      min.z + (size.z) * random())
  }

  override def getIntersectionDepth(ray: Ray): Double = {
    val tx1 = (min.x - ray.start.x) / ray.normal.x
    val tx2 = (max.x - ray.start.x) / ray.normal.x

    var tmin = Math.min(tx1, tx2)
    var tmax = Math.max(tx1, tx2)

    val ty1 = (min.y - ray.start.y) / ray.normal.y
    val ty2 = (max.y - ray.start.y) / ray.normal.y

    tmin = Math.max(tmin, Math.min(ty1, ty2))
    tmax = Math.min(tmax, Math.max(ty1, ty2))

    val tz1 = (min.z - ray.start.z) / ray.normal.z
    val tz2 = (max.z - ray.start.z) / ray.normal.z

    tmin = Math.max(tmin, Math.min(tz1, tz2))
    tmax = Math.min(tmax, Math.max(tz1, tz2))

    if (tmax < tmin || tmin < 0.0001) return Double.PositiveInfinity

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
    val sharedSize = (sharedMax - sharedMin)
    return new AABB(sharedMin + sharedSize / 2, sharedSize)
  }

  def surfaceArea: Double = {
    return (size.x * (size.y + size.z) + size.y * size.z) * 2
  }

  def contains(v: Vec3d): Boolean = {
    return min.x <= v.x && v.x <= max.x && min.y <= v.y && v.y <= max.y && min.z <= v.z && v.z <= max.z
  }

  override def enclosingAABB: AABB = this
}