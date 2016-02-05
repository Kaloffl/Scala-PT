package kaloffl.spath.scene.structure

import kaloffl.spath.scene.shapes.Bounded
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.math.Ray

class ClippingNode(val childNode: SceneNode, bounds: AABB) extends SceneNode with Bounded {

  override def getBounds = bounds

  override def getIntersection(ray: Ray, maxDepth: Double): Intersection = {
    if (bounds.contains(ray.start) || bounds.getIntersectionDepth(ray) < maxDepth) {
      return childNode.getIntersection(ray, maxDepth)
    }
    return Intersection.NullIntersection
  }
}