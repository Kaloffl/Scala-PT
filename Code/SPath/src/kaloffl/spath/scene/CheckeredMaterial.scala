package kaloffl.spath.scene

import kaloffl.spath.math.Vec3d

class CheckeredMaterial(val colorA: Vec3d, val colorB: Vec3d) extends Material {

  def directionValue(position: Double): Boolean = {
    (position * 5).toInt % 2 == 0
  }

  def reflectanceAt(worldPos: Vec3d, normal: Vec3d): Vec3d = {
    if (directionValue(worldPos.x) ^ directionValue(worldPos.y) ^ directionValue(worldPos.z)) {
      colorA
    } else {
      colorB
    }
  }

  def reflectedNormal(
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    random: () ⇒ Float): Vec3d = {

    return surfaceNormal.randomHemisphere(random)
  }
}