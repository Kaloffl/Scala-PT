package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.SurfaceInfo

class DirectionalLightMaterial(val color: Color,
                               direction: Vec3d,
                               val limit: Float) extends Material(color, DummyFunction) {

  val dir = -direction

  def getEmittance(incomingNormal: Vec3d): Color = {
    val angle = Math.max(limit, incomingNormal.dot(dir).toFloat)
    if (1 == limit) {
      if (1 == angle) {
        return color
      }
      return Color.Black
    }
    return color * (1 - (1 - angle) / (1 - limit))
  }

  override def getInfo(incomingNormal: Vec3d,
                       worldPos: ⇒ Vec3d,
                       surfaceNormal: ⇒ Vec3d,
                       textureCoordinate: ⇒ Vec2d,
                       airRefractiveIndex: Float,
                       random: DoubleSupplier): SurfaceInfo = {
    return new SurfaceInfo(
      Color.Black,
      getEmittance(incomingNormal),
      TerminatedScattering)
  }
}