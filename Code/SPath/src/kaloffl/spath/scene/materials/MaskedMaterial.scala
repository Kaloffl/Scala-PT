package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.SurfaceInfo

class MaskedMaterial(
    val matA: Material,
    val matB: Material,
    val mask: Mask) extends Material(Color.Black, Color.Black, DummyFunction) {

  def useA(num: Int, factor: Double): Boolean = {
    0 == (num * factor).toInt - ((num - 1) * factor).toInt
  }

  override def getInfo(incomingNormal: Vec3d,
                       surfaceNormal: ⇒ Vec3d,
                       textureCoordinate: ⇒ Vec2d,
                       refractiveIndex: Float,
                       random: DoubleSupplier): SurfaceInfo = {

    if (random.getAsDouble < mask.maskAmount(textureCoordinate)) {
      matA.getInfo(
        incomingNormal,
        surfaceNormal,
        textureCoordinate,
        refractiveIndex,
        random)
    } else {
      matB.getInfo(
        incomingNormal,
        surfaceNormal,
        textureCoordinate,
        refractiveIndex,
        random)
    }
  }

}

trait Mask {
  def maskAmount(textureCoord: ⇒ Vec2d): Float
}

class TextureMask(val texture: Texture) extends Mask {
  override def maskAmount(textureCoord: ⇒ Vec2d): Float = {
    val tc = textureCoord
    return texture(tc.x.toFloat, tc.y.toFloat).r
  }
}