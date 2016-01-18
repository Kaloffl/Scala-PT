package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.SurfaceInfo

class MaskedMaterial(
    val matA: Material,
    val matB: Material,
    val mask: Mask) extends Material(Color.Black, DummyFunction) {

  def useA(num: Int, factor: Double): Boolean = {
    0 == (num * factor).toInt - ((num - 1) * factor).toInt
  }

  override def getInfo(incomingNormal: Vec3d,
                       worldPos: ⇒ Vec3d,
                       surfaceNormal: ⇒ Vec3d,
                       textureCoordinate: ⇒ Vec2d,
                       refractiveIndex: Float,
                       random: DoubleSupplier): SurfaceInfo = {

    if (random.getAsDouble < mask.maskAmount(worldPos, textureCoordinate)) {
      matA.getInfo(
        incomingNormal,
        worldPos,
        surfaceNormal,
        textureCoordinate,
        refractiveIndex,
        random)
    } else {
      matB.getInfo(
        incomingNormal,
        worldPos,
        surfaceNormal,
        textureCoordinate,
        refractiveIndex,
        random)
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