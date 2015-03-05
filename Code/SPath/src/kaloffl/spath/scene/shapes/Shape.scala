package kaloffl.spath.scene.shapes

import kaloffl.spath.tracing.Ray
import kaloffl.spath.math.Vec3d

/**
 * The general contract of a shape that can be rendered by the path tracer.
 * It must be able to supply the normal vector of a point on its surface, a
 * random point inside the shape and tell the length a ray must travel in order
 * to intersect the shape.
 */
trait Shape {

  /**
   * Returns a normalized vector pointing straight away from the surface at
   * the given point. (e.g. if the point lies on a sphere, the normal vector
   * points from the center of the sphere through the given point)
   */
  def getNormal(point: Vec3d): Vec3d

  /**
   * Returns a random point inside the shape.
   */
  def getRandomInnerPoint(random: () ⇒ Float): Vec3d

  /**
   * Tells the distance a ray must travel in order to intersect with the shape.
   * If the ray can't intersect the shape, a large or infinite number is
   * returned.
   */
  def getIntersectionDepth(ray: Ray): Double
}