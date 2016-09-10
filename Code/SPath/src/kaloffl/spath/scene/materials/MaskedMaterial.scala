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
                              outsideIor: Color,
                              random: DoubleSupplier): (Array[Vec3d], Array[Float]) = {

    val (matANormals, matAWeights) =
      matA.getScattering(
        incomingNormal,
        surfaceNormal,
        uv,
        outsideIor,
        random)

    val (matBNormals, matBWeights) =
      matB.getScattering(
        incomingNormal,
        surfaceNormal,
        uv,
        outsideIor,
        random)

    val combinedNormals = new Array[Vec3d](matANormals.length + matBNormals.length)
    System.arraycopy(matANormals, 0, combinedNormals, 0, matANormals.length)
    System.arraycopy(matBNormals, 0, combinedNormals, matANormals.length, matBNormals.length)

    val weightA = mask.maskAmount(uv)
    val weightB = 1 - weightA

    val combinedWeights = new Array[Float](matAWeights.length + matBWeights.length)
    for (i <- matAWeights.indices) combinedWeights(i) = matAWeights(i) * weightA
    for (i <- matBWeights.indices) combinedWeights(i + matAWeights.length) = matBWeights(i) * weightB

    return (combinedNormals, combinedWeights)
  }

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Color): Color = {

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