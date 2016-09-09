package kaloffl.spath.scene.structure

import kaloffl.spath.math.Ray
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.{Bounded, Shape}
import kaloffl.spath.tracing.Intersection

class SingleShape(val shape: Shape, material: Material) extends SceneNode {

  override def getShapes: Seq[(Shape, Material)] = Seq((shape, material))

  override def getIntersection(ray: Ray, maxDepth: Double): Intersection = {
    val depth = shape.getIntersectionDepth(ray, maxDepth)
    val point = ray.atDistance(depth)
    if (depth < maxDepth) new Intersection(
      depth,
      material,
      () ⇒ shape.getNormal(point),
      () ⇒ shape.getTextureCoordinate(point))
    else Intersection.NullIntersection
  }
}

class BoundedSingleShape(shape: Shape with Bounded, material: Material) extends SingleShape(shape, material) with Bounded {
  override def getBounds = shape.getBounds
}