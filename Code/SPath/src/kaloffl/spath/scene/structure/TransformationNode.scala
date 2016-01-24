package kaloffl.spath.scene.structure

import kaloffl.spath.math.Transformation
import kaloffl.spath.math.Ray
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.math.Vec3d

class TransformationNode(transformation: Transformation,
                         childNode: SceneNode) extends SceneNode {

  override def enclosingAABB: AABB = {
    val childAABB = childNode.enclosingAABB
    val tMin = transformation.transformPoint(childAABB.min)
    val tMax = transformation.transformPoint(childAABB.max)
    return new AABB(tMin.min(tMax), tMax.max(tMin))
  }

  override def getIntersection(ray: Ray, maxDepth: Double): Intersection = {
    val childIntersection = childNode.getIntersection(
      transformation.transformRayInverse(ray),
      maxDepth)
    if (childIntersection.hitObject) {
      new Intersection(
        childIntersection.depth,
        childIntersection.material,
        () ⇒ transformation.transformDirection(childIntersection.normal()),
        childIntersection.textureCoordinate)
    } else {
      Intersection.NullIntersection
    }
  }

  override def getIntersectionDepth(ray: Ray): Double = {
    childNode.getIntersectionDepth(transformation.transformRayInverse(ray))
  }

  override def getIntersectionDepth(ray: Ray, maxDepth: Double): Double = {
    childNode.getIntersectionDepth(
      transformation.transformRayInverse(ray),
      maxDepth)
  }
}