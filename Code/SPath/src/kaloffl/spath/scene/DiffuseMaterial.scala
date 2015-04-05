package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context

class DiffuseMaterial(val color: Color) extends Material {

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

    return surfaceNormal.randomHemisphere(context.random)
  }
}