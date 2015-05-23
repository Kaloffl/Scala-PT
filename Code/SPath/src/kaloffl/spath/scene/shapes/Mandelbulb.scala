package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3d

class Mandelbulb(position: Vec3d, power: Int) extends EstimatedShape {

  override def estimateDepth(point: Vec3d): Double = {
    var wx = point.x
    var wy = point.y
    var wz = point.z
    var r = Math.sqrt(wx * wx + wy * wy + wz * wz)
    var dr = 1.0
    var i = 0
    while (r < 5 && i < 3) {
      val rp = Math.pow(r, power - 1.0)
      val wr = rp * r
      val wo = Math.acos(wy / r) * power
      val wi = Math.atan2(wx, wz) * power
      dr = rp * power * dr + 1.0

      val swo = Math.sin(wo) * wr
      wx = point.x + swo * Math.sin(wi)
      wy = point.y + Math.cos(wo) * wr
      wz = point.z + swo * Math.cos(wi)
      r = Math.sqrt(wx * wx + wy * wy + wz * wz)
      i += 1
    }
    return 0.5 * Math.log(r) * r / dr
  }

  override def enclosingAABB: AABB = {
    return AABB(position, Vec3d(2.5))
  }
}