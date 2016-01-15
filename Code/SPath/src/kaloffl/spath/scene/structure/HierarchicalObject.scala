package kaloffl.spath.scene.structure

import kaloffl.spath.math.Ray
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.scene.shapes.AABB

class HierarchicalObject(val children: Array[_ <: SceneNode]) extends SceneNode {

  val hull = AABB.enclosing[SceneNode](children, _.enclosingAABB)

  def getIntersection(ray: Ray, maxDepth: Double): Intersection = {
    var closestIntersection = Intersection.nullIntersection
    var closestDist = maxDepth
    var i = 0
    while (i < children.length) {
      val intersection = children(i).getIntersection(ray, closestDist)
      if (intersection.hitObject) {
        closestIntersection = intersection
        closestDist = intersection.depth
      }
      i += 1
    }
    return closestIntersection
  }

  def enclosingAABB: AABB = hull
}