package kaloffl.spath.scene.structure

import kaloffl.spath.math.Ray
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.{AABB, Bounded, Shape}
import kaloffl.spath.tracing.Intersection

class NodeList[T <: SceneNode](val children: Array[T]) extends SceneNode {

  override def getShapes: Seq[(Shape, Material)] = children.flatMap(_.getShapes)

  def getIntersection(ray: Ray, maxDepth: Double): Intersection = {
    var closestIntersection = Intersection.NullIntersection
    var closestDist = maxDepth
    var i = 0
    while (i < children.length) {
      val intersection = children(i).getIntersection(ray, closestDist)
      if (intersection.hitObject) {
        closestIntersection = intersection
        closestDist = intersection.depth
      }
      i += 1
    }
    return closestIntersection
  }
}

class BoundedNodeList[T <: SceneNode with Bounded](children: Array[T]) extends NodeList[T](children) with Bounded {
  val hull = AABB.enclosing[Bounded](children, _.getBounds)
  override def getBounds: AABB = hull
}