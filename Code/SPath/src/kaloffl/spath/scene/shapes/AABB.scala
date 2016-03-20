package kaloffl.spath.scene.shapes

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Ray
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d

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

class AABB(val min: Vec3d, val max: Vec3d) extends Shape with Bounded with Closed {

  val bounds = Array(min, max)

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
    if(point.x <= min.x + 0.0001) {
      Vec3d.Right
    } else if(point.x <= max.x - 0.0001) {
      if(point.y < min.y + 0.0001) {
        Vec3d.Down
      } else if(point.y <= max.y - 0.0001) {
        if(point.z < min.z + 0.0001) {
          Vec3d.Back
        } else if(point.z <= max.z - 0.0001) {
          // this case should not happen but unfortunately it does sometimes 
          // due to imprecise calculations. Just returning something and 
          // acting like everything is fine should be ok, because it happens 
          // so rarely
          Vec3d.Up
        } else {
          Vec3d.Front
        }
      } else {
        Vec3d.Up
      }
    } else {
      Vec3d.Left
    }
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
    // http://people.csail.mit.edu/amy/papers/box-jgt.pdf
    val txmin = (bounds(ray.sx).x - ray.start.x) * ray.inverseNormal.x
    val txmax = (bounds(1 - ray.sx).x - ray.start.x) * ray.inverseNormal.x
    val tymin = (bounds(ray.sy).y - ray.start.y) * ray.inverseNormal.y
    val tymax = (bounds(1 - ray.sy).y - ray.start.y) * ray.inverseNormal.y
    if (txmin > tymax || tymin > txmax) return Double.PositiveInfinity
    val tmin = if (txmin > tymin) txmin else tymin
    val tmax = if (txmax < tymax) txmax else tymax
    val tzmin = (bounds(ray.sz).z - ray.start.z) * ray.inverseNormal.z
    val tzmax = (bounds(1 - ray.sz).z - ray.start.z) * ray.inverseNormal.z
    if (tmin > tzmax || tzmin > tmax) return Double.PositiveInfinity
    val tmin2 = if (tzmin > tmin) tzmin else tmin
    val tmax2 = if (tzmax < tmax) tzmax else tmax
    if (tmin2 < 0.0001) {
      if (tmax2 < 0.0001) return Double.PositiveInfinity
      return tmax2
    }
    return tmin2
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

  override def contains(v: Vec3d): Boolean = {
    return min.x <= v.x && v.x <= max.x &&
      min.y <= v.y && v.y <= max.y &&
      min.z <= v.z && v.z <= max.z
  }

  def overlaps(other: AABB): Boolean = {
    return min.x < other.max.x &&
           max.x > other.min.x &&
           min.y < other.max.y &&
           max.y > other.min.y &&
           min.z < other.max.z &&
           max.z > other.min.z
  }
}