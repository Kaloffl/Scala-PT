package kaloffl.spath.scene.structure

import kaloffl.spath.bvh.Bvh
import kaloffl.spath.math.Ray
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.{Bounded, Intersectable, Shape}
import kaloffl.spath.tracing.Intersection

class NodeBvh (val bvh: Bvh[_ <: SceneNode]) extends SceneNode with Bounded {

  override def getShapes: Seq[(Shape, Material)] = {
    def getLeafs[T <: Intersectable](bvh: Bvh[T]): Seq[T] = {
      if (null == bvh.children) {
        return bvh.elements
      } else {
        bvh.children.flatMap(getLeafs)
      }
    }
    return getLeafs(bvh).flatMap(_.getShapes)
  }

  override def getBounds = bvh.hull

  override def getIntersection(ray: Ray, maxDist: Double): Intersection = {
    val (node, depth) = bvh.findClosestObject(ray, maxDist)
    if (null != node) node.getIntersection(ray, maxDist)
    else Intersection.NullIntersection
  }
}