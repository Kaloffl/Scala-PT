package kaloffl.spath.scene.structure

import kaloffl.spath.math.Ray
import kaloffl.spath.scene.shapes.{AABB, Bounded}
import kaloffl.spath.tracing.Intersection

class ClippingNode(val childNode: SceneNode, bounds: AABB) extends SceneNode with Bounded {

  override def getBounds = bounds

  override def getIntersection(ray: Ray, maxDepth: Double): Intersection = {
    if (bounds.contains(ray.start) || bounds.getIntersectionDepth(ray) < maxDepth) {
      childNode.getIntersection(ray, maxDepth)
    } else {
      Intersection.NullIntersection
    }
  }
}