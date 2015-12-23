package kaloffl.spath.scene.shapes

import kaloffl.spath.tracing.Ray

trait Intersectable {
  /**
   * Tells the distance a ray must travel in order to intersect with the object.
   * If the ray can't intersect the object, a large or infinite number is
   * returned.
   */
  def getIntersectionDepth(ray: Ray): Double
}