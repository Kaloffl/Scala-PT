package kaloffl.spath.scene.structure

import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.tracing.Ray
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.scene.shapes.AABB

class FlatObject(val shapes: Array[Shape], val material: Material) extends SceneNode {

  def this(shape: Shape, material: Material) {
    this(Array(shape), material)
  }
  
  override val enclosingAABB = AABB[Shape](shapes, _.enclosingAABB)
  
  override def getIntersection(ray: Ray, maxDepth: Double): Intersection = {
    var closestDist = maxDepth
    var closestShape: Shape = null
    var i = 0
    while(i < shapes.length) {
      val depth = shapes(i).getIntersectionDepth(ray)
      
      if(depth < closestDist) {
        closestDist = depth
        closestShape = shapes(i)
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