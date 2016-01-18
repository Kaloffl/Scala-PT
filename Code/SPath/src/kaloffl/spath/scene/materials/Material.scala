package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.SurfaceInfo

class Material(reflectance: Color, scatterFunction: ScatterFunction) {

  def scatterProbability: Double = 0.0

  def refractiveIndex: Float = 1.0f

  def getAbsorbtion(worldPos: Vec3d, random: DoubleSupplier): Color = Color.Black

  def getInfo(incomingNormal: Vec3d,
              worldPos: ⇒ Vec3d,
              surfaceNormal: ⇒ Vec3d,
              textureCoordinate: ⇒ Vec2d,
              airRefractiveIndex: Float,
              random: DoubleSupplier): SurfaceInfo = {
    new SurfaceInfo(
      reflectance,
      Color.Black,
      scatterFunction.outDirections(
        incomingNormal,
        surfaceNormal,
        airRefractiveIndex,
        random))
  }

}