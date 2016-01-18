package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation
import kaloffl.spath.scene.SurfaceInfo
import kaloffl.spath.math.Vec2d

class Material(reflectance: Color, scatterFunction: ScatterFunction) {

  def scatterProbability: Double = 0.0

  def refractiveIndex: Float = 1.0f

  def getAbsorbtion(worldPos: Vec3d, context: Context): Color = Color.Black

  def getEmittance(worldPos: Vec3d,
                   surfaceNormal: Vec3d,
                   incomingNormal: Vec3d,
                   context: Context): Color = Color.Black

  def getInfo(incomingNormal: Vec3d,
              worldPos: ⇒ Vec3d,
              surfaceNormal: ⇒ Vec3d,
              textureCoordinate: ⇒ Vec2d,
              airRefractiveIndex: Float,
              context: Context): SurfaceInfo = {
    new SurfaceInfo(
      reflectance,
      getEmittance(
        worldPos,
        surfaceNormal,
        incomingNormal,
        context),
      scatterFunction.outDirections(
        incomingNormal,
        surfaceNormal,
        airRefractiveIndex,
        context.random))
  }

}