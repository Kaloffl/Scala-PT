package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.SurfaceInfo

class DiffuseTexturedMaterial(texture: Texture) extends Material(Color.Black, DiffuseFunction) {

  override def getInfo(incomingNormal: Vec3d,
                       worldPos: ⇒ Vec3d,
                       surfaceNormal: ⇒ Vec3d,
                       textureCoordinate: ⇒ Vec2d,
                       airRefractivityIndex: Float,
                       random: DoubleSupplier): SurfaceInfo = {
    // call-by-name parameters are evaluated each time their value is used,
    // so we need to cache the result to avoid multiple calls
    val tc = textureCoordinate
    new SurfaceInfo(
      texture(tc.x.toFloat, tc.y.toFloat),
      Color.Black,
      DiffuseFunction.outDirections(
        incomingNormal,
        surfaceNormal,
        airRefractivityIndex,
        random))
  }
}