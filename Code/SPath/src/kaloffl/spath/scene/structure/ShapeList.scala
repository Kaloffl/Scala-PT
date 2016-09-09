package kaloffl.spath.scene.structure

import kaloffl.spath.math.Ray
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.{AABB, Bounded, Shape}
import kaloffl.spath.tracing.Intersection

class ShapeList[T <: Shape](val shapes: Array[T], val material: Material) extends SceneNode {

  override def getShapes: Seq[(Shape, Material)] = shapes.map((_, material))

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
    val point = ray.atDistance(closestDist)
    if(null == closestShape) {
      Intersection.NullIntersection
    } else {
      new Intersection(
          closestDist,
          material,
          () => closestShape.getNormal(point),
          () => closestShape.getTextureCoordinate(point))
    }
  }
}

class BoundedShapeList[T <: Shape with Bounded](shapes: Array[T], material: Material) extends ShapeList[T](shapes, material) with Bounded {
    override val getBounds = AABB.enclosing[Bounded](shapes, _.getBounds)
}