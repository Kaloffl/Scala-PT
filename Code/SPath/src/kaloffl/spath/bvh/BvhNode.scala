package kaloffl.spath.bvh

import kaloffl.spath.tracing.Ray
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.Material
import kaloffl.spath.scene.shapes.Shape

class BvhNode(
    val childA: BvhNode,
    val childB: BvhNode,
    val shapes: Array[Shape],
    val materials: Array[Material],
    val aabb: AABB,
    val level: Int) {

  def depth(ray: Ray): Double = if (aabb.contains(ray.start)) 0.0 else aabb.getIntersectionDepth(ray)

  def intersectObjects(ray: Ray): Intersection = {
    var minDepth: Double = Double.PositiveInfinity
    var hitIndex: Int = -1
    var i = 0
    while (i < shapes.length) {
      val depth = shapes(i).getIntersectionDepth(ray)
      if (depth < minDepth) {
        minDepth = depth
        hitIndex = i
      }
      i += 1
    }
    if (0 > hitIndex) return null
    return new Intersection(minDepth, shapes(hitIndex), materials(hitIndex))
  }
}
