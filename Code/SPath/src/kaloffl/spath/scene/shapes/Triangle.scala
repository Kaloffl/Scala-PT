package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Ray

/**
 * @author Lars
 */
class Triangle(val vertA: Vec3d, val vertB: Vec3d, val vertC: Vec3d) extends Shape {

  //  val edgeB = vertC - vertA
  //  val edgeA = vertB - vertA
  val normal = (vertB - vertA).cross(vertC - vertA).normalize

  override def enclosingAABB: AABB = {
    val minX = Math.min(vertA.x, Math.min(vertB.x, vertC.x))
    val minY = Math.min(vertA.y, Math.min(vertB.y, vertC.y))
    val minZ = Math.min(vertA.z, Math.min(vertB.z, vertC.z))
    val min = Vec3d(minX, minY, minZ)

    val maxX = Math.max(vertA.x, Math.max(vertB.x, vertC.x))
    val maxY = Math.max(vertA.y, Math.max(vertB.y, vertC.y))
    val maxZ = Math.max(vertA.z, Math.max(vertB.z, vertC.z))
    val max = Vec3d(maxX, maxY, maxZ)

    return new AABB(min, max)
  }

  override def getIntersectionDepth(ray: Ray): Double = {
    val P = ray.normal.cross(vertC - vertA)
    val d = (vertB - vertA).dot(P)

    if (d > -0.000001 && d < 0.000001)
      return Double.PositiveInfinity

    val T = ray.start - vertA
    val u = T.dot(P) / d

    if (u < 0.0f || u > 1.0f)
      return Double.PositiveInfinity

    val Q = T.cross(vertB - vertA)
    val v = ray.normal.dot(Q) / d

    if (v < 0.0f || u + v > 1.0f)
      return Double.PositiveInfinity

    val t = (vertC - vertA).dot(Q) / d

    if (t < 0.000001)
      return Double.PositiveInfinity

    return t
  }

  override def getNormal(point: Vec3d): Vec3d = normal

  override def getRandomInnerPoint(random: () ⇒ Double): Vec3d = {
    ???
  }
}