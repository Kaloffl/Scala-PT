package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3f
import kaloffl.spath.tracing.Ray
import kaloffl.spath.math.Vec2f

class Plane(val normal: Vec3f, val distance: Float) extends Shape {

  override def getNormal(point: Vec3f): Vec3f = normal

  override def getIntersectionDepth(ray: Ray): Float = {
    val d = normal.dot(ray.normal)
    val t = -(normal.dot(ray.start) + distance) / d
    if (0.0001f < t) return t
    return Float.PositiveInfinity
  }
}