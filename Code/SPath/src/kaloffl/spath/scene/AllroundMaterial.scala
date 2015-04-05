package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context

class AllroundMaterial(
    emittance: Color,
    reflectance: Color,
    reflectivity: Float,
    refractivity: Float,
    refractivityIndex: Float,
    glossiness: Float) extends Material {

  override def terminatesPath: Boolean = {
    (0 != emittance.r2) | (0 != emittance.g2) | (0 != emittance.b2)
  }

  override def reflectanceAt(
    worldPos: Vec3d,
    normal: Vec3d,
    context: Context): Color = {

    if (terminatesPath) {
      return emittance
    }
    return reflectance
  }

  override def reflectNormal(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    context: Context): Vec3d = {

    val randomHs = surfaceNormal.randomHemisphere(context.random) * glossiness

    if (1.0f == glossiness) return randomHs

    if (context.random() * (reflectivity + refractivity) < refractivity) {
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