package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Vec2d

class DirectionalLightMaterial(
    val color: Color,
    emittance: Float,
    direction: Vec3d,
    val limit: Float) extends Material {

  val emitted = color * emittance
  val dir = -direction

  override def minEmittance = emitted

  override def getEmittance(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    depth: Double,
    context: Context): Color = {

    val angle = Math.max(limit, incomingNormal.dot(dir).toFloat)
    if (1 == limit) {
      if (1 == angle) {
        return emitted
      }
      return Color.BLACK
    }
    return emitted * (1 - (1 - angle) / (1 - limit))
  }

  override def getInfo(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    depth: Double,
    refractivityIndex: Double,
    context: Context): SurfaceInfo = {

    val emit = getEmittance(worldPos, surfaceNormal, incomingNormal, depth, context)
    new SurfaceInfo(
      emit,
      emit,
      surfaceNormal.randomHemisphere(Vec2d.random(context.random)))
  }
}