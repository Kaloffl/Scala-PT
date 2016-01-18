package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.SurfaceInfo

class LightMaterial(
    val color: Color,
    val attenuation: Attenuation) extends Material(color, DummyFunction) {

  override def getInfo(incomingNormal: Vec3d,
                       worldPos: ⇒ Vec3d,
                       surfaceNormal: ⇒ Vec3d,
                       textureCoordinate: ⇒ Vec2d,
                       airRefractiveIndex: Float,
                       random: DoubleSupplier): SurfaceInfo = {
    return new SurfaceInfo(Color.Black, color, TerminatedScattering)
  }
}