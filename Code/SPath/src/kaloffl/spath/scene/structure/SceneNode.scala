package kaloffl.spath.scene.structure

import kaloffl.spath.bvh.Bvh
import kaloffl.spath.bvh.BvhBuilder
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.tracing.Ray

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
    new HierarchicalObject(objects)
  }
}

trait SceneNode {

  def getIntersection(ray: Ray, maxDepth: Double): Intersection

  def enclosingAABB: AABB
}