package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation

class AllroundMaterial(
    color: Color,
    emittance: Float,
    reflectivity: Float,
    refractivity: Float,
    refractivityIndex: Float,
    glossiness: Float) extends Material {

  override def minEmittance: Color = color * emittance

  def reflectNormal(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    context: Context): Vec3d = {

    val randomHs = surfaceNormal.randomHemisphere(context.random) * glossiness

    if (1.0f == glossiness) return randomHs

    if (context.random.getAsDouble * (reflectivity + refractivity) < refractivity) {
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

  override def emittanceAt(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    context: Context): Color = {

    color * emittance
  }

  override def attenuation = Attenuation.none

  override def getInfo(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    context: Context): SurfaceInfo = {

    new SurfaceInfo(
      color,
      color * emittance,
      Attenuation.none,
      reflectNormal(worldPos, surfaceNormal, incomingNormal, context),
      true)
  }
}