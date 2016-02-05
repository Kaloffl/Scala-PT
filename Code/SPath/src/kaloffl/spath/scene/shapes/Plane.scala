package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Ray
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d

class Plane(normal: Vec3d, distance: Float) extends Shape {
 
  override def getIntersectionDepth(ray: Ray): Double = {
    val d = normal.dot(ray.normal)
    val t = -(normal.dot(ray.start) + distance) / d
    if (0.0001 < t) return t
    return Double.PositiveInfinity
  }
  
  override def getNormal(point: Vec3d): Vec3d = normal

  override def getTextureCoordinate(point: Vec3d): Vec2d = ???
}