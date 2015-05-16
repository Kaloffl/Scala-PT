package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Vec2d
import kaloffl.spath.scene.SurfaceInfo

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

    val axis = surfaceNormal.randomConeSample(Vec2d.random(context.random), glossiness, 0.0)
    new SurfaceInfo(
      color,
      Color.BLACK,
      if (axis.dot(incomingNormal) > 0) {
        incomingNormal.reflect(-axis)
      } else {
        incomingNormal.reflect(axis)
      })
  }
}