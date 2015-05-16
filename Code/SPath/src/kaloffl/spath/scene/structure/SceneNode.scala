package kaloffl.spath.scene.structure

import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Ray
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.bvh.Bvh

object SceneNode {

  def apply(shape: Shape, material: Material): SceneNode = {
    new FlatObject(shape, material)
  }

  def apply(shapes: Array[Shape], material: Material): SceneNode = {
    if (shapes.length > Bvh.MAX_LEAF_SIZE) {
      new Bvh(shapes, material)
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