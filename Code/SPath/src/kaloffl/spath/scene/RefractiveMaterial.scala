package kaloffl.spath.scene

import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Color

class RefractiveMaterial(
    val color: Color,
    val refractivityIndex: Double,
    val glossiness: Double) extends Material {

  override def terminatesPath: Boolean = false

  override def reflectanceAt(worldPos: Vec3d, normal: Vec3d): Color = color

  override def reflectedNormal(
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    random: () ⇒ Float): Vec3d = {

    val randomness = surfaceNormal.randomHemisphere(random)
    val refracted = incomingNormal.refract(surfaceNormal, 1.0f, refractivityIndex)
      if (0.0f < glossiness) {
        return (refracted + randomness).normalize
      }
      return refracted
  }
}