package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Vec2d
import kaloffl.spath.scene.SurfaceInfo

class DiffuseTexturedMaterial(texture: Texture) extends Material(Color.Black, DiffuseFunction) {

  override def getInfo(worldPos: Vec3d,
                       surfaceNormal: Vec3d,
                       incomingNormal: Vec3d,
                       textureCoordinate: Vec2d,
                       airRefractivityIndex: Double,
                       context: Context): SurfaceInfo = {
    new SurfaceInfo(
      texture(textureCoordinate.x.toFloat, textureCoordinate.y.toFloat),
      Color.Black,
      DiffuseFunction.outDirection(
        incomingNormal,
        surfaceNormal,
        airRefractivityIndex,
        context.random))
  }
}