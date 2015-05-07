package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation

class RefractiveMaterial(
    val color: Color,
    override val refractivityIndex: Double,
    val glossiness: Double) extends Material {

  override def getAbsorbtion(woldPos: Vec3d, contexxt: Context): Color = color

  override def getInfo(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    depth: Double,
    airRefractivityIndex: Double,
    context: Context): SurfaceInfo = {

    val randomness = surfaceNormal.randomHemisphere(context.random)
    val axis = surfaceNormal * (1 - glossiness) + randomness * glossiness
    val (i1, i2, surf) = if (axis.dot(incomingNormal) > 0) {
      (refractivityIndex, 1.0, -axis)
    } else {
      (1.0, refractivityIndex, axis)
    }

    new SurfaceInfo(
      color,
      Color.BLACK,
      if (context.random.getAsDouble < incomingNormal.refractance(surf, i1, i2)) {
        incomingNormal.reflect(surf);
      } else {
        incomingNormal.refract(surf, i1, i2)
      },
      true)
  }
}