package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Vec2d, Vec3d}

class MaskedMaterial(
    val matA: Material,
    val matB: Material,
    val mask: Mask) extends Material {

  override def getScattering(
                              incomingNormal: Vec3d,
                              surfaceNormal: Vec3d,
                              uv: Vec2d,
                              outsideIor: Float,
                              random: DoubleSupplier): Array[Vec3d] = {

    val matAResult =
      matA.getScattering(
        incomingNormal,
        surfaceNormal,
        uv,
        outsideIor,
        random)

    val matBResult =
      matB.getScattering(
        incomingNormal,
        surfaceNormal,
        uv,
        outsideIor,
        random)

    val result = new Array[Vec3d](matAResult.length + matBResult.length)
    for (i <- matAResult.indices) result(i) = matAResult(i)
    for (i <- matBResult.indices) result(i + matAResult.length) = matBResult(i)

    return result
  }

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
  def maskAmount(textureCoord: ⇒ Vec2d): Float
}

class TextureMask(val texture: Texture) extends Mask {
  override def maskAmount(textureCoord: ⇒ Vec2d): Float = {
    val tc = textureCoord
    return texture(tc.x.toFloat, tc.y.toFloat).r
  }
}