package kaloffl.spath.scene.structure

import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.math.Ray
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.scene.shapes.AABB

class ShapeList(val shapes: Array[_ <: Shape], val material: Material) extends SceneNode {

  override val enclosingAABB = AABB.enclosing[Shape](shapes, _.enclosingAABB)

  override def getIntersection(ray: Ray, maxDepth: Double): Intersection = {
    var closestDist = maxDepth
    var closestShape: Shape = null
    var i = 0
    while (i < shapes.length) {
      val depth = shapes(i).getIntersectionDepth(ray)
      if (depth < closestDist) {
        closestDist = depth
        closestShape = shapes(i)
      }
      i += 1
    }
    return new Intersection(
      closestDist,
      material,
      closestShape)
  }
}