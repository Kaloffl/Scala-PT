package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Ray
import java.util.function.DoubleSupplier

/**
 * The general contract of a shape that can be rendered by the path tracer.
 * It must be able to supply the normal vector of a point on its surface, a
 * random point inside the shape and tell the length a ray must travel in order
 * to intersect the shape.
 */
trait Shape extends Intersectable with Enclosable {

  /**
   * Returns a normalized vector pointing straight away from the surface at
   * the given point. (e.g. if the point lies on a sphere, the normal vector
   * points from the center of the sphere through the given point)
   */
  def getNormal(point: Vec3d): Vec3d

  def randomSurfacePoint(rng: DoubleSupplier): Vec3d
}