package kaloffl.spath.scene.materials

import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.SurfaceInfo
import kaloffl.spath.tracing.Context

class LightMaterial(
    val color: Color,
    val attenuation: Attenuation) extends Material(color, DummyFunction) {

  override def getEmittance(worldPos: Vec3d,
                            surfaceNormal: Vec3d,
                            incomingNormal: Vec3d,
                            context: Context): Color = color

  override def getInfo(incomingNormal: Vec3d,
                       worldPos: ⇒ Vec3d,
                       surfaceNormal: ⇒ Vec3d,
                       textureCoordinate: ⇒ Vec2d,
                       airRefractiveIndex: Float,
                       context: Context): SurfaceInfo = {
    return new SurfaceInfo(Color.Black, color, TerminatedScattering)
  }
}