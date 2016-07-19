package kaloffl.spath.scene.structure

import kaloffl.spath.bvh.Bvh
import kaloffl.spath.math.Ray
import kaloffl.spath.scene.shapes.Bounded
import kaloffl.spath.tracing.Intersection

class NodeBvh (val bvh: Bvh[_ <: SceneNode]) extends SceneNode with Bounded {

  override def getBounds = bvh.hull

  override def getIntersection(ray: Ray, maxDist: Double): Intersection = {
    val (node, depth) = bvh.findClosestObject(ray, maxDist)
    if (null != node) node.getIntersection(ray, maxDist)
    else Intersection.NullIntersection
  }
}