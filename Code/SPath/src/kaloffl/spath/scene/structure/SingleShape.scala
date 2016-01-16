package kaloffl.spath.scene.structure

import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.math.Ray
import kaloffl.spath.tracing.Intersection

class SingleShape(shape: Shape, material: Material) extends SceneNode {
  def enclosingAABB = shape.enclosingAABB
  def getIntersection(ray: Ray, maxDepth: Double): Intersection = {
    val depth = shape.getIntersectionDepth(ray, maxDepth)
    if(depth < maxDepth) new Intersection(depth, material, shape)
    else Intersection.NullIntersection
  }
}