package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Ray
import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Vec2d
import kaloffl.spath.scene.materials.Texture
import kaloffl.spath.math.Color

class NormalMappedShape(base: Shape, normalMap: Texture) extends Shape {

  override def getNormal(point: Vec3d): Vec3d = {
    val z = base.getNormal(point)
    val x = z.ortho.normalize
    val y = z.cross(x).normalize
    val tc = getTextureCoordinate(point)
    val c = normalMap(tc.x.toFloat, tc.y.toFloat)
    val n = Vec3d(c.r, c.g, c.b) * 2 - 1
    return (x * n.x + y * n.y + z * n.z).normalize
  }

  override def getTextureCoordinate(point: Vec3d): Vec2d = {
    base.getTextureCoordinate(point)
  }
  
  override def getIntersectionDepth(ray: Ray): Double = {
    base.getIntersectionDepth(ray)
  }

  override def getIntersectionDepth(ray: Ray, maxDepth: Double): Double = {
    base.getIntersectionDepth(ray, maxDepth)
  }

  override def enclosingAABB = base.enclosingAABB
}