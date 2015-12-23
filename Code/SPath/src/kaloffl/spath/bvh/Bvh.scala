package kaloffl.spath.bvh

import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.tracing.Ray

object Bvh {
  val MAX_LEAF_SIZE = 8
}

class Bvh(root: BvhNode[Shape], material: Material) extends SceneNode {

  override def enclosingAABB: AABB = root.hull

  override def getIntersection(ray: Ray, maxDist: Double): Intersection = {
    val stack = new ValuedArrayStack[BvhNode[Shape]]()
    val rootDepth = root.hullDepth(ray)
    if (rootDepth > maxDist) return Intersection.nullIntersection

    var closestShape: Shape = null
    var closestDepth = maxDist

    stack add (root, rootDepth)
    while (!stack.empty) {
      val (node, depth) = stack.pop
      if (depth >= closestDepth) {
        return new Intersection(closestDepth, material, closestShape)
      }
      if (node.isLeaf) {
        val (shape, depth) = node.intersectElements(ray, maxDist)
        if (depth < closestDepth) {
          closestShape = shape
          closestDepth = depth
        }
      } else {
        var i = 0
        while (i < node.children.length) {
          val child = node.children(i)
          val depth = child.hullDepth(ray)
          if (Double.PositiveInfinity != depth && depth < closestDepth) {
            stack add (child, depth)
          }
          i += 1
        }
      }
    }
    return new Intersection(closestDepth, material, closestShape)
  }
}

