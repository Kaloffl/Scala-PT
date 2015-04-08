package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation

class MaskedMaterial(
    val matA: Material,
    val matB: Material,
    val mask: Mask) extends Material {

  def useA(num: Int, factor: Double): Boolean = {
    0 == (num * factor).toInt - ((num - 1) * factor).toInt
  }

  override def attenuation = Attenuation.none

  override def minEmittance: Color = {
    val meA = matA.minEmittance
    val meB = matB.minEmittance
    new Color(
      Math.min(meA.r2, meB.r2),
      Math.min(meA.g2, meB.g2),
      Math.min(meA.b2, meB.b2))
  }

  override def emittanceAt(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    context: Context): Color = {

    if (useA(context.passNum, mask.maskAmount(worldPos))) {
      matA.emittanceAt(worldPos, surfaceNormal, context)
    } else {
      matB.emittanceAt(worldPos, surfaceNormal, context)
    }
  }

  def getInfo(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    context: Context): SurfaceInfo = {

    if (useA(context.passNum, mask.maskAmount(worldPos))) {
      matA.getInfo(worldPos, surfaceNormal, incomingNormal, context)
    } else {
      matB.getInfo(worldPos, surfaceNormal, incomingNormal, context)
    }
  }

}

trait Mask {
  def maskAmount(pos: Vec3d): Float
}

class CheckeredMask(val size: Float) extends Mask {
  override def maskAmount(pos: Vec3d): Float = {
    if (((pos.x * size).toInt % 2 == 0) ^ pos.x > 0 ^
      ((pos.y * size).toInt % 2 == 0) ^ pos.y > 0 ^
      ((pos.z * size).toInt % 2 == 0) ^ pos.z > 0) {
      1.0f
    } else {
      0.0f
    }
  }
}