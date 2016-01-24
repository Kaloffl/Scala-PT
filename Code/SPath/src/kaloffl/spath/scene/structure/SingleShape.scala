package kaloffl.spath.scene.structure

import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.math.Ray
import kaloffl.spath.tracing.Intersection

class SingleShape(shape: Shape, material: Material) extends SceneNode {
  def enclosingAABB = shape.enclosingAABB
  def getIntersection(ray: Ray, maxDepth: Double): Intersection = {
    val depth = shape.getIntersectionDepth(ray, maxDepth)
    val point = ray.atDistance(depth)
    if(depth < maxDepth) new Intersection(
        depth, 
        material, 
        () => shape.getNormal(point),
        () => shape.getTextureCoordinate(point))
    else Intersection.NullIntersection
  }
}