package kaloffl.spath.math

/**
 * A ray consists of a staring point and a direction.
 */
class Ray(val start: Vec3d, val normal: Vec3d) {
  
  def atDistance(distance: Double): Vec3d = start + normal * distance
}