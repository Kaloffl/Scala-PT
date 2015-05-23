package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Vec2d
import kaloffl.spath.scene.SurfaceInfo

class LightMaterial(
    val color: Color,
    emittance: Float,
    val attenuation: Attenuation) extends Material {

  val emitted = color * emittance

  override def minEmittance = emitted

  override def getEmittance(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    depth: Double,
    context: Context): Color = emitted

  override def getInfo(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    depth: Double,
    refractivityIndex: Double,
    context: Context): SurfaceInfo = {

    new SurfaceInfo(
      Color.WHITE,
      emitted,
      surfaceNormal.randomHemisphere(Vec2d.random(context.random)))
  }
}