package kaloffl.spath.math

/**
 * A ray consists of a staring point and a direction.
 */
class Ray(val start: Vec3d, val normal: Vec3d) {
  
  val inverseNormal = Vec3d(1 / normal.x, 1 / normal.y, 1 / normal.z)
  
  val sx = if(inverseNormal.x < 0) 1 else 0
  val sy = if(inverseNormal.y < 0) 1 else 0
  val sz = if(inverseNormal.z < 0) 1 else 0
  
  def atDistance(distance: Double): Vec3d = start + normal * distance
}