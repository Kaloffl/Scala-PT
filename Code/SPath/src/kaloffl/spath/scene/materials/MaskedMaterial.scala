package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Vec2d, Vec3d}
import kaloffl.spath.sampler.Sampler

class MaskedMaterial(
    val matA: Material,
    val matB: Material,
    val mask: Mask) extends Material {

  val samplers = new Array[Sampler](matA.getSamplers.length + matB.getSamplers.length)
  System.arraycopy(matA.getSamplers, 0, samplers, 0, matA.getSamplers.length)
  System.arraycopy(matB.getSamplers, 0, samplers, matA.getSamplers.length, matB.getSamplers.length)

  override def getSamplers: Array[_ <: Sampler] = samplers

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float): Color = {

    val weightA = mask.maskAmount(uv)
    val resultA = matA.evaluateBSDF(
        toEye,
        surfaceNormal,
        toLight,
        uv,
        outsideIor)
    val resultB = matB.evaluateBSDF(
        toEye,
        surfaceNormal,
        toLight,
        uv,
        outsideIor)
    return resultA * weightA + resultB * (1 - weightA)
  }

}

trait Mask {
  def maskAmount(textureCoord: Vec2d): Float
}

class TextureMask(val texture: Texture) extends Mask {
  override def maskAmount(textureCoord: Vec2d): Float = {
    return texture(textureCoord.x.toFloat, textureCoord.y.toFloat).r
  }
}