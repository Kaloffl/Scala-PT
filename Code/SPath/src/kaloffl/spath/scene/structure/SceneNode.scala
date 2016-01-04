package kaloffl.spath.scene.structure

import kaloffl.spath.bvh.Bvh
import kaloffl.spath.bvh.BvhBuilder
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.math.Ray
import kaloffl.spath.scene.shapes.Intersectable
import kaloffl.spath.scene.shapes.Enclosable

object SceneNode {

  def apply(shape: Shape, material: Material): SceneNode = {
    new FlatObject(shape, material)
  }

  def apply(shapes: Array[Shape], material: Material): SceneNode = {
    if (shapes.length > Bvh.MAX_LEAF_SIZE) {
      BvhBuilder.buildBvh(shapes, material)
    } else {
      new FlatObject(shapes, material)
    }
  }

  def apply(objects: Array[SceneNode]): SceneNode = {
    if(objects.length < Bvh.MAX_LEAF_SIZE) {
      new HierarchicalObject(objects)
    } else {
      BvhBuilder.buildBvh(objects)
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