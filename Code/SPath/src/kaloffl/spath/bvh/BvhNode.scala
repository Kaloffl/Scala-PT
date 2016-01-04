package kaloffl.spath.bvh

import kaloffl.spath.math.Ray
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.shapes.Intersectable

class BvhNode[T <: Intersectable](
    val children: Array[BvhNode[T]],
    val elements: Array[T],
    val hull: AABB,
    val level: Int) {

  def isLeaf = (null != elements)

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
}
