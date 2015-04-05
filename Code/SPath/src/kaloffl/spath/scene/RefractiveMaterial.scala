package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context

class RefractiveMaterial(
    val color: Color,
    val refractivityIndex: Double,
    val glossiness: Double) extends Material {

  override def reflectanceAt(
    worldPos: Vec3d,
    normal: Vec3d,
    context: Context): Color = {
    return color
  }

  override def reflectNormal(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    context: Context): Vec3d = {

    val randomness = surfaceNormal.randomHemisphere(context.random)
    val axis = surfaceNormal * (1 - glossiness) + randomness * glossiness
    if (axis.dot(incomingNormal) > 0) {
      return incomingNormal.refract(-axis, 1.0f, refractivityIndex)
    }
    return incomingNormal.refract(axis, 1.0f, refractivityIndex)
  }
}