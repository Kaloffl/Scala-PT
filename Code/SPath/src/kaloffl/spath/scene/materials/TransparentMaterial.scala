package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Vec2d
import java.util.function.DoubleSupplier
import kaloffl.spath.scene.SurfaceInfo

class TransparentMaterial(
    color: Color,
    absorbtionDepth: Float = 1,
    override val scatterProbability: Double = 0,
    override val refractiveIndex: Float = 1,
    glossiness: Float = 0) extends Material(Color.White, Color.Black, new RefractFunction(refractiveIndex, glossiness)) {

  override val absorbtion = color / absorbtionDepth

  override def getInfo(incomingNormal: Vec3d,
                       surfaceNormal: ⇒ Vec3d,
                       textureCoordinate: ⇒ Vec2d,
                       airRefractiveIndex: Float,
                       random: DoubleSupplier): SurfaceInfo = {
    new SurfaceInfo(
      Color.White,
      scatterFunction.outDirections(
        incomingNormal,
        surfaceNormal,
        airRefractiveIndex,
        random))
  }
}