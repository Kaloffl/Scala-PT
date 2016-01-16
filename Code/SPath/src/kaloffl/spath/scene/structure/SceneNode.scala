package kaloffl.spath.scene.structure

import kaloffl.spath.bvh.BvhBuilder
import kaloffl.spath.math.Ray
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.Enclosable
import kaloffl.spath.scene.shapes.Intersectable
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.tracing.Intersection

object SceneNode {

  def apply(shape: Shape, material: Material): SceneNode = {
    new SingleShape(shape, material)
  }

  def apply(shapes: Array[_ <: Shape], material: Material): SceneNode = {
    if (shapes.length > BvhBuilder.MaxLeafSize) {
      new ShapeBvh(BvhBuilder.buildTree(shapes), material)
    } else {
      new ShapeList(shapes, material)
    }
  }

  def apply(objects: Array[_ <: SceneNode]): SceneNode = {
    if (objects.length > BvhBuilder.MaxLeafSize) {
      new NodeBvh(BvhBuilder.buildTree(objects))
    } else {
      new NodeList(objects)
    }
  }
}

trait SceneNode extends Intersectable with Enclosable {

  def getIntersection(ray: Ray, maxDepth: Double): Intersection

  override def getIntersectionDepth(ray: Ray): Double = {
    getIntersection(ray, Double.PositiveInfinity).depth
  }

  override def getIntersectionDepth(ray: Ray, maxDepth: Double): Double = {
    getIntersection(ray, maxDepth).depth
  }
}