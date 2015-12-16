package kaloffl.spath.bvh

import kaloffl.spath.tracing.Ray
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.structure.SceneNode

class BvhNode(
    val children: Array[BvhNode],
    val elements: Array[Shape],
    val hull: AABB,
    val level: Int) {

  def hullDepth(ray: Ray): Double = {
    if (hull.contains(ray.start)) return 0.0
    return hull.getIntersectionDepth(ray)
  }

  def intersectElements(ray: Ray, maxDepth: Double, material: Material): Intersection = {
    var closestDist = maxDepth
    var closestShape: Shape = null
    var i = 0
    while (i < elements.length) {
      val depth = elements(i).getIntersectionDepth(ray)

      if (depth < closestDist) {
        closestDist = depth
        closestShape = elements(i)
      }
      i += 1
    }
    if (null == closestShape) {
      return null
    }
    return new Intersection(
      closestDist,
      material,
      closestShape)
  }
}
