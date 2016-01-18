package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation
import kaloffl.spath.scene.SurfaceInfo
import kaloffl.spath.math.Vec2d

class MaskedMaterial(
    val matA: Material,
    val matB: Material,
    val mask: Mask) extends Material(Color.Black, DummyFunction) {

  def useA(num: Int, factor: Double): Boolean = {
    0 == (num * factor).toInt - ((num - 1) * factor).toInt
  }

  override def minEmittance: Color = {
    val meA = matA.minEmittance
    val meB = matB.minEmittance
    new Color(
      Math.min(meA.r2, meB.r2),
      Math.min(meA.g2, meB.g2),
      Math.min(meA.b2, meB.b2))
  }

  override def getEmittance(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    context: Context): Color = {

    if (useA(context.passNum, mask.maskAmount(worldPos, Vec2d()))) {
      matA.getEmittance(worldPos, surfaceNormal, incomingNormal, context)
    } else {
      matB.getEmittance(worldPos, surfaceNormal, incomingNormal, context)
    }
  }

  override def getInfo(incomingNormal: Vec3d,
                       worldPos: ⇒ Vec3d,
                       surfaceNormal: ⇒ Vec3d,
                       textureCoordinate: ⇒ Vec2d,
                       refractivityIndex: Double,
                       context: Context): SurfaceInfo = {

    if (useA(context.passNum, mask.maskAmount(worldPos, textureCoordinate))) {
      matA.getInfo(
        incomingNormal,
        worldPos,
        surfaceNormal,
        textureCoordinate,
        refractivityIndex,
        context)
    } else {
      matB.getInfo(
        incomingNormal,
        worldPos,
        surfaceNormal,
        textureCoordinate,
        refractivityIndex,
        context)
    }
  }

}

trait Mask {
  def maskAmount(pos: Vec3d, textureCoord: ⇒ Vec2d): Float
}

class CheckeredMask(val size: Double, val offset: Vec3d = Vec3d.Origin) extends Mask {
  override def maskAmount(pos: Vec3d, textureCoord: ⇒ Vec2d): Float = {
    val mx = pos.x * size + offset.x
    val my = pos.y * size + offset.y
    val mz = pos.z * size + offset.z
    if ((mx.toInt % 2 == 0) ^ mx > 0 ^ (my.toInt % 2 == 0) ^ my > 0 ^ (mz.toInt % 2 == 0) ^ mz > 0) {
      1.0f
    } else {
      0.0f
    }
  }
}

class GridMask(val size: Double, thickness: Double, val offset: Vec3d = Vec3d.Origin) extends Mask {
  override def maskAmount(pos: Vec3d, textureCoord: ⇒ Vec2d): Float = {
    val mx = Math.abs(pos.x * size + offset.x) % 1
    val my = Math.abs(pos.y * size + offset.y) % 1
    val mz = Math.abs(pos.z * size + offset.z) % 1
    if (mx < thickness || mx > 1 - thickness || my < thickness || my > 1 - thickness || mz < thickness || mz > 1 - thickness) {
      1.0f
    } else {
      0.0f
    }
  }
}

class TextureMask(val texture: Texture) extends Mask {
  override def maskAmount(pos: Vec3d, textureCoord: ⇒ Vec2d): Float = {
    val tc = textureCoord
    return texture(tc.x.toFloat, tc.y.toFloat).r
  }
}