package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation

class TransparentMaterial(
    val color: Color,
    override val absorbtionCoefficient: Double,
    override val scatterPropability: Double,
    override val refractivityIndex: Double) extends Material {

  override def getAbsorbtion(worldPos: Vec3d, context: Context): Color = color

  override def getInfo(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    depth: Double,
    airRefractivityIndex: Double,
    context: Context): SurfaceInfo = {

    val (i1, i2, surf) = if (surfaceNormal.dot(incomingNormal) > 0) {
      (refractivityIndex, 1.0, -surfaceNormal)
    } else {
      (1.0, refractivityIndex, surfaceNormal)
    }

    new SurfaceInfo(
      Color.WHITE,
      Color.BLACK,
      if (context.random.getAsDouble < incomingNormal.refractance(surf, i1, i2)) {
        incomingNormal.reflect(surf);
      } else {
        incomingNormal.refract(surf, i1, i2)
      })
  }
}