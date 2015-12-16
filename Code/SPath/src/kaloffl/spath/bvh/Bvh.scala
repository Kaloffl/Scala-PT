package kaloffl.spath.bvh

import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.tracing.Ray

object Bvh {
  val MAX_LEAF_SIZE = 16
}

class Bvh(objects: Array[Shape], material: Material) extends SceneNode {

  val root = BvhBuilder.buildHierarchy(objects)

  override def enclosingAABB: AABB = root.hull

  override def getIntersection(ray: Ray, maxDist: Double): Intersection = {
    class NodeIntersection(val depth: Double, val node: BvhNode)
    val stack: SortedArrayStack[NodeIntersection] = new SortedArrayStack(_.depth < _.depth)
    val rootDepth = root.hullDepth(ray)
    if (rootDepth > maxDist) return null

    var closestIntersection: Intersection = null
    var closestDist= maxDist

    stack add new NodeIntersection(rootDepth, root)
    while (!stack.empty) {
      val head = stack.pop
      if (head.depth >= closestDist) {
        return closestIntersection
      }
      val headNode = head.node
      if (null == headNode.children) {
        val intersection = headNode.intersectElements(ray, maxDist, material)
        if (null != intersection && closestDist > intersection.depth) {
          closestIntersection = intersection
          closestDist = intersection.depth
        }
      } else {
        var i = 0
        while (i < headNode.children.length) {
          val child = headNode.children(i)
          val depth = child.hullDepth(ray)
          if (Double.PositiveInfinity != depth && depth < closestDist) {
            stack add new NodeIntersection(depth, child)
          }
          i += 1
        }
      }
    }
    return closestIntersection
  }
}

