package kaloffl.spath.scene

import kaloffl.spath.math.Vec3d

/**
 * @author Lars
 */
class ReflectiveMaterial(val color: Vec3d, val glossiness: Double) extends Material {
  def reflectanceAt(worldPos: Vec3d, normal: Vec3d): Vec3d = color

  def reflectedNormal(
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    random: () ⇒ Float): Vec3d = {

    val randomness = surfaceNormal.randomHemisphere(random)
    val axis = surfaceNormal * (1 - glossiness) + randomness * glossiness
    if (axis.dot(incomingNormal) > 0) {
      return incomingNormal.reflect(-axis)
    }
    return incomingNormal.reflect(axis)
  }
}