package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation

/**
 * @author Lars
 */
class ReflectiveMaterial(val color: Color, val glossiness: Double) extends Material {

  override def getInfo(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    depth: Double,
    refractivityIndex: Double,
    context: Context): SurfaceInfo = {

    val randomness = surfaceNormal.randomHemisphere(context.random)
    val axis = surfaceNormal * (1 - glossiness) + randomness * glossiness
    new SurfaceInfo(
      color,
      Color.BLACK,
      if (axis.dot(incomingNormal) > 0) {
        incomingNormal.reflect(-axis)
      } else {
        incomingNormal.reflect(axis)
      },
      true)
  }
}