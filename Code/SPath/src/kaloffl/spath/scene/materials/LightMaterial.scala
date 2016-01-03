package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Vec2d
import kaloffl.spath.scene.SurfaceInfo

class LightMaterial(
    val color: Color,
    val attenuation: Attenuation) extends Material(color, DummyFunction) {

  override def minEmittance = color

  override def getEmittance(worldPos: Vec3d,
                            surfaceNormal: Vec3d,
                            incomingNormal: Vec3d,
                            depth: Double,
                            context: Context): Color = color

  override def getInfo(worldPos: Vec3d,
                       surfaceNormal: Vec3d,
                       incomingNormal: Vec3d,
                       depth: Double,
                       airRefractivityIndex: Double,
                       context: Context): SurfaceInfo = {
    return new SurfaceInfo(Color.BLACK, color, Vec3d.ORIGIN)
  }
}