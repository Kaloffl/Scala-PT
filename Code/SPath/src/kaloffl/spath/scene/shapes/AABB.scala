package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Ray

/**
 * AABB stands for Axis Aligned Bounding Box and is a very simple and 
 * computationally fast shape to test for intersection. However as the name
 * suggests, it is not possible to rotate this shape since the sides are aligned
 * along the three axis.
 * 
 * @author Lars Donner
 */
class AABB(center: Vec3d, size: Vec3d) extends Shape {

  val min = center - size / 2
  val max = center + size / 2

  override def getNormal(point: Vec3d): Vec3d = {
    if (point.x > max.x) {
      return Vec3d.LEFT;
    } else if (point.x < min.x) {
      return Vec3d.RIGHT;
    } else if (point.y > max.y) {
      return Vec3d.UP;
    } else if (point.y < min.y) {
      return Vec3d.DOWN;
    } else if (point.z > max.z) {
      return Vec3d.FRONT;
    } else if (point.z < min.z) {
      return Vec3d.BACK;
    }
    return Vec3d.UP;
  }

  override def getRandomInnerPoint(random: () ⇒ Float): Vec3d = {
    return Vec3d(
      min.x + (max.x - min.x) * random.apply(),
      min.y + (max.y - min.y) * random.apply(),
      min.z + (max.z - min.z) * random.apply())
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
}