package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation

class DiffuseMaterial(val color: Color) extends Material {

  override def getInfo(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    depth: Double,
    refractivityIndex: Double,
    context: Context): SurfaceInfo = {

    new SurfaceInfo(
      color,
      Color.BLACK,
      surfaceNormal.weightedHemisphere(context.random),
      false)
  }
}