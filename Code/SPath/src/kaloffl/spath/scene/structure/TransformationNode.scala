package kaloffl.spath.scene.structure

import kaloffl.spath.math.{Ray, Transformation}
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.{AABB, Bounded, Shape}
import kaloffl.spath.tracing.Intersection

class TransformationNode(val transformation: Transformation,
                         val childNode: SceneNode) extends SceneNode {

  override def getShapes: Seq[(Shape, Material)] = childNode.getShapes

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

class BoundedTransformationNode(transformation: Transformation,
                                childNode: SceneNode with Bounded)
    extends TransformationNode(transformation, childNode)
    with Bounded {

  override def getBounds: AABB = {
    val childAABB = childNode.getBounds
    val tMin = transformation.transformPoint(childAABB.min)
    val tMax = transformation.transformPoint(childAABB.max)
    return new AABB(tMin.min(tMax), tMax.max(tMin))
  }
}