package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context

class MaskedMaterial(
    val matA: Material,
    val matB: Material,
    val mask: Mask) extends Material {

  def useA(num: Int, factor: Double): Boolean = {
    0 == (num * factor).toInt - ((num - 1) * factor).toInt
  }

  override def reflectanceAt(
    worldPos: Vec3d,
    normal: Vec3d,
    context: Context): Color = {

    if (useA(context.passNum, mask.maskAmount(worldPos))) {
      matA.reflectanceAt(worldPos, normal, context)
    } else {
      matB.reflectanceAt(worldPos, normal, context)
    }
  }

  def reflectNormal(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    context: Context): Vec3d = {

    if (useA(context.passNum, mask.maskAmount(worldPos))) {
      matA.reflectNormal(worldPos, surfaceNormal, incomingNormal, context)
    } else {
      matB.reflectNormal(worldPos, surfaceNormal, incomingNormal, context)
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