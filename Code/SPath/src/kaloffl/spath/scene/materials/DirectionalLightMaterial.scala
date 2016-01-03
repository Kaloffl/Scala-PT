package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Vec2d
import kaloffl.spath.scene.SurfaceInfo

class DirectionalLightMaterial(
    val color: Color,
    direction: Vec3d,
    val limit: Float) extends Material(color, DummyFunction) {

  val dir = -direction

  override def minEmittance = color

  override def getEmittance(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    depth: Double,
    context: Context): Color = {

    val angle = Math.max(limit, incomingNormal.dot(dir).toFloat)
    if (1 == limit) {
      if (1 == angle) {
        return color
      }
      return Color.BLACK
    }
    return color * (1 - (1 - angle) / (1 - limit))
  }

  override def getInfo(worldPos: Vec3d,
                       surfaceNormal: Vec3d,
                       incomingNormal: Vec3d,
                       depth: Double,
                       airRefractivityIndex: Double,
                       context: Context): SurfaceInfo = {
    return new SurfaceInfo(
      Color.BLACK,
      getEmittance(worldPos, surfaceNormal, incomingNormal, depth, context),
      Vec3d.ORIGIN)
  }
}