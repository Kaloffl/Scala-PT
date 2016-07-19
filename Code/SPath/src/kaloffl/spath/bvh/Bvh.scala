package kaloffl.spath.bvh

import kaloffl.spath.math.Ray
import kaloffl.spath.scene.shapes.{AABB, Intersectable}

class Bvh[T <: Intersectable](
    val children: Array[Bvh[T]],
    val elements: Array[T],
    val hull: AABB,
    val level: Int) {

  def isLeaf = null != elements

  def hullDepth(ray: Ray): Double = {
    if (hull.contains(ray.start)) return 0.0
    return hull.getIntersectionDepth(ray)
  }

  def intersectElements(ray: Ray, maxDepth: Double): (T, Double) = {
    var closestObject = null.asInstanceOf[T]
    var closestDist = maxDepth
    var i = 0
    while (i < elements.length) {
      val depth = elements(i).getIntersectionDepth(ray)
      if (depth < closestDist) {
        closestDist = depth
        closestObject = elements(i)
      }
      i += 1
    }
    return (closestObject, closestDist)
  }

  def findClosestObject(ray: Ray, maxDist: Double): (T, Double) = {
    val stack = new ValuedArrayStack[Bvh[T]]()

    var closestShape = null.asInstanceOf[T]
    var closestDepth = maxDist

    stack add (this, hullDepth(ray))
    while (!stack.empty) {
      val (node, depth) = stack.pop
      if (depth >= closestDepth) {
        return (closestShape, closestDepth)
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
    return (closestShape, closestDepth)
  }
}
