package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Ray
import java.util.function.DoubleSupplier
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Mat3d
import kaloffl.spath.math.Mat4d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Vec4d

/**
 * @author Lars
 */
class Triangle(val vertA: Vec3d, vertB: Vec3d, vertC: Vec3d) extends Shape with Bounded with Projectable {

  val edgeA = vertB - vertA
  val edgeB = vertC - vertA

  val normal = (edgeA cross edgeB) normalize

  val mat = {
    val m = Mat4d(
      edgeA.x, edgeB.x, normal.x, vertA.x,
      edgeA.y, edgeB.y, normal.y, vertA.y,
      edgeA.z, edgeB.z, normal.z, vertA.z,
      0, 0, 0, 1).inverse
    Array(
      m.m11, m.m12, m.m13, m.m14,
      m.m21, m.m22, m.m23, m.m24,
      m.m31, m.m32, m.m33, m.m34)
  }

  override def getBounds: AABB = {
    val vertB = edgeA + vertA
    val vertC = edgeB + vertA
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

  override def getTextureCoordinate(point: Vec3d): Vec2d = {
    val relative = (point - vertA) / (edgeA + edgeB)
    return Vec2d(relative.x, relative.y)
  }

  override def getIntersectionDepth(ray: Ray): Double = {
    val ox = ray.start.x
    val oy = ray.start.y
    val oz = ray.start.z

    val nx = ray.normal.x
    val ny = ray.normal.y
    val nz = ray.normal.z

    var t1 = mat(8); var t2 = mat(9); var t3 = mat(10)
    val t = -(t1 * ox + t2 * oy + t3 * oz + mat(11)) / (t1 * nx + t2 * ny + t3 * nz)
    if (t < 0.0001) return Double.PositiveInfinity

    t1 = mat(0); t2 = mat(1); t3 = mat(2)
    val u = (t1 * ox + t2 * oy + t3 * oz + mat(3)) + t * (t1 * nx + t2 * ny + t3 * nz)

    t1 = mat(4); t2 = mat(5); t3 = mat(6)
    val v = (t1 * ox + t2 * oy + t3 * oz + mat(7)) + t * (t1 * nx + t2 * ny + t3 * nz)

    if (u >= 0 && v >= 0 && (u + v) < 1) t
    else Double.PositiveInfinity
  }
  
  override def getNormal(point: Vec3d): Vec3d = normal

  override def getSolidAngle(point: Vec3d): Double = {
    val a = vertA - point
    val b = a + edgeA
    val c = a + edgeB
    val na = (c cross b)
    val nb = (a cross c)
    val nc = (b cross a)
    val la = na length
    val lb = nb length
    val lc = nc length
    val aa = Math.acos((nb dot nc) / (lb * lc))
    val ab = Math.acos((nc dot na) / (lc * la))
    val ac = Math.acos((na dot nb) / (la * lb))
    return 1 - (aa + ab + ac) / (Math.PI * 2)
  }

  override def createRandomRay(start: Vec3d, random: DoubleSupplier): Ray = {
    val r1 = random.getAsDouble
    val t = random.getAsDouble
    val r2 = if (r1 + t > 1) 1 - t else t
    return new Ray(start, (vertA - start + edgeA * r1 + edgeB * r2).normalize)
  }
}