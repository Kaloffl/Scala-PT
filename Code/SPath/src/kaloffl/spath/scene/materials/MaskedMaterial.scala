package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Vec2d, Vec3d}

class MaskedMaterial(
    val matA: Material,
    val matB: Material,
    val mask: Mask) extends Material {

  def useA(num: Int, factor: Double): Boolean = {
    0 == (num * factor).toInt - ((num - 1) * factor).toInt
  }

  override def getScattering(
                              incomingNormal: Vec3d,
                              surfaceNormal: Vec3d,
                              uv: Vec2d,
                              currentRefractiveIndex: Float,
                              random: DoubleSupplier): Array[Scattering] = {

    if (random.getAsDouble < mask.maskAmount(uv)) {
      matA.getScattering(
        incomingNormal,
        surfaceNormal,
        uv,
        currentRefractiveIndex,
        random)
    } else {
      matB.getScattering(
        incomingNormal,
        surfaceNormal,
        uv,
        currentRefractiveIndex,
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