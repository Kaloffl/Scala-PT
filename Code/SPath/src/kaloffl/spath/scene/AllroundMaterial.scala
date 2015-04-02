package kaloffl.spath.scene

import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Color

class AllroundMaterial(
    emittance: Color,
    reflectance: Color,
    reflectivity: Float,
    refractivity: Float,
    refractivityIndex: Float,
    glossiness: Float) extends Material {

  override def terminatesPath: Boolean = {
    (0 == emittance.r) | (0 == emittance.g) | (0 == emittance.b)
  }

  override def reflectanceAt(worldPos: Vec3d, normal: Vec3d): Color = {
    if (terminatesPath) {
      return emittance
    }
    return reflectance
  }

  override def reflectedNormal(
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    random: () ⇒ Float): Vec3d = {

    val randomHs = surfaceNormal.randomHemisphere(random) * glossiness

    if (1.0f == glossiness) return randomHs

    if (random() * (reflectivity + refractivity) < refractivity) {
      val refracted = incomingNormal.refract(surfaceNormal, 1.0f, refractivityIndex)
      if (0.0f < glossiness) {
        return (refracted + randomHs).normalize
      }
      return refracted
    }

    val reflected = incomingNormal.reflect(surfaceNormal)
    if (0.0f < glossiness) {
      val direction = reflected + randomHs
      if (direction.dot(surfaceNormal) < 0) {
        return (reflected + randomHs.reflect(reflected)).normalize
      }
      return direction.normalize
    }
    return reflected
  }
}