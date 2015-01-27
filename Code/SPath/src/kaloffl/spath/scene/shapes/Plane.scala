package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Ray

/**
 * A plane of infinite size with some orientation and a distance to the origin 
 * point of the scene.
 * 
 * @author Lars Donner
 */
class Plane(val normal: Vec3d, val distance: Float) extends Shape {

  override def getNormal(point: Vec3d): Vec3d = normal

  override def getRandomInnerPoint(random: () ⇒ Float): Vec3d = {
    val antiNormal = -normal
    val rnd = antiNormal.randomHemisphere(random)
    return normal.cross(rnd) * random.apply - normal * distance
  }

  override def getIntersectionDepth(ray: Ray): Double = {
    val d = normal.dot(ray.normal)
    val t = -(normal.dot(ray.start) + distance) / d
    if (0.0001f < t) return t
    return Float.PositiveInfinity
  }
}