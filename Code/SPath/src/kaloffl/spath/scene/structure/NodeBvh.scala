package kaloffl.spath.scene.structure

import kaloffl.spath.bvh.Bvh
import kaloffl.spath.math.Ray
import kaloffl.spath.tracing.Intersection

class NodeBvh (val bvh: Bvh[_ <: SceneNode]) extends SceneNode {

  override def enclosingAABB = bvh.hull

  override def getIntersection(ray: Ray, maxDist: Double): Intersection = {
    val (node, depth) = bvh.findClosestObject(ray, maxDist)
    if (null != node) node.getIntersection(ray, maxDist)
    else Intersection.NullIntersection
  }
}