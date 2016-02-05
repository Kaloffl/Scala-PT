package kaloffl.spath.scene.structure

import kaloffl.spath.bvh.BvhBuilder
import kaloffl.spath.math.Ray
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.Bounded
import kaloffl.spath.scene.shapes.Intersectable
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.tracing.Intersection

object SceneNode {
  
  def apply(shape: Shape with Bounded, material: Material): BoundedSingleShape = {
      new BoundedSingleShape(shape.asInstanceOf[Shape with Bounded], material)
  }

  def apply(shapes: Array[_ <: Shape with Bounded], material: Material): SceneNode with Bounded = {
      if (shapes.length > BvhBuilder.MaxLeafSize) {
        new ShapeBvh(BvhBuilder.buildTree(shapes), material)
      } else {
        new BoundedShapeList(shapes, material)
      }
  }

  def apply(objects: Array[_ <: SceneNode with Bounded]): SceneNode with Bounded = {
      if (objects.length > BvhBuilder.MaxLeafSize) {
        new NodeBvh(BvhBuilder.buildTree(objects))
      } else {
        new BoundedNodeList(objects)
      }
  }
}

object BoundlessNode {

  def apply(shape: Shape, material: Material): SceneNode = {
      new SingleShape(shape, material)
  }

  def apply(shapes: Array[_ <: Shape], material: Material): SceneNode = {
      new ShapeList(shapes, material)
  }

  def apply(objects: Array[_ <: SceneNode]): SceneNode = {
      new NodeList(objects)
  }
}

trait SceneNode extends Intersectable {

  def getIntersection(ray: Ray, maxDepth: Double): Intersection

  override def getIntersectionDepth(ray: Ray): Double = {
    getIntersection(ray, Double.PositiveInfinity).depth
  }

  override def getIntersectionDepth(ray: Ray, maxDepth: Double): Double = {
    getIntersection(ray, maxDepth).depth
  }
}