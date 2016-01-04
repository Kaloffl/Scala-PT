package kaloffl.spath.bvh

import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.math.Ray
import kaloffl.spath.scene.shapes.Intersectable

object Bvh {
  // TODO move constant into BvhBuilder file
  val MAX_LEAF_SIZE = 8
}

abstract class Bvh[T <: Intersectable](root: BvhNode[T]) extends SceneNode {
  override def enclosingAABB: AABB = root.hull
}

class ShapeBvh(root: BvhNode[Shape], material: Material) extends Bvh(root) {

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
          if (depth < closestDepth) {
            stack add (child, depth)
          }
          i += 1
        }
      }
    }
    return new Intersection(closestDepth, material, closestShape)
  }
}

class ObjectBvh(root: BvhNode[SceneNode]) extends Bvh(root) {

  override def getIntersection(ray: Ray, maxDist: Double): Intersection = {
    val stack = new ValuedArrayStack[BvhNode[SceneNode]]()
    val rootDepth = root.hullDepth(ray)
    if (rootDepth > maxDist) return Intersection.nullIntersection

    var closestIntersection = Intersection.nullIntersection
    var closestDist = maxDist

    stack add (root, rootDepth)
    while (!stack.empty) {
      val (node, depth) = stack.pop
      if (depth >= closestIntersection.depth) {
        return closestIntersection
      }
      if (node.isLeaf) {
        var i = 0
        while (i < node.elements.length) {
          val intersection = node.elements(i).getIntersection(ray, closestDist)
          if (intersection.hitObject) {
            closestIntersection = intersection
            closestDist = intersection.depth
          }
          i += 1
        }
      } else {
        var i = 0
        while (i < node.children.length) {
          val child = node.children(i)
          val depth = child.hullDepth(ray)
          if (depth < closestDist) {
            stack add (child, depth)
          }
          i += 1
        }
      }
    }
    return closestIntersection
  }
}

