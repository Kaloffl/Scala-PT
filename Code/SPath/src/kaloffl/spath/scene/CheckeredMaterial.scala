package kaloffl.spath.scene

import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Color

class CheckeredMaterial(val colorA: Color, val colorB: Color) extends Material {

  def directionValue(position: Double): Boolean = {
    ((position * 2).toInt % 2 == 0) ^ position > 0
  }

  def reflectanceAt(worldPos: Vec3d, normal: Vec3d): Color = {
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