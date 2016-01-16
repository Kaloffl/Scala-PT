package kaloffl.spath.scene.structure

import kaloffl.spath.bvh.Bvh
import kaloffl.spath.math.Ray
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.tracing.Intersection

class ShapeBvh(val bvh: Bvh[_ <: Shape], material: Material) extends SceneNode {

  override def enclosingAABB = bvh.hull

  override def getIntersection(ray: Ray, maxDist: Double): Intersection = {
    val (shape, depth) = bvh.findClosestObject(ray, maxDist)
    if(null != shape) new Intersection(depth, material, shape)
    else Intersection.NullIntersection
  }
}