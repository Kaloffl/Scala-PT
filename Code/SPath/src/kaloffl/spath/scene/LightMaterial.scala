package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation

class LightMaterial(
    val color: Color,
    emittance: Float,
    radius: Double) extends Material {

  val emitted = color * emittance
  val attenuation = Attenuation.radius(radius)

  override def minEmittance = emitted

  override def emittanceAt(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    context: Context): Color = emitted

  override def getInfo(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    context: Context): SurfaceInfo = {

    new SurfaceInfo(
      color,
      emitted,
      attenuation,
      surfaceNormal.randomHemisphere(context.random),
      false)
  }
}