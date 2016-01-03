package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation
import kaloffl.spath.scene.SurfaceInfo

class Material(reflectance: Color, scatterFunction: ScatterFunction) {

  def minEmittance: Color = Color.BLACK

  def absorbtionCoefficient: Double = 0.0
  def scatterPropability: Double = 0.0

  def refractivityIndex: Double = 1.0

  def getAbsorbtion(worldPos: Vec3d,
                    context: Context): Color = Color.BLACK

  def getEmittance(worldPos: Vec3d,
                   surfaceNormal: Vec3d,
                   incomingNormal: Vec3d,
                   depth: Double,
                   context: Context): Color = Color.BLACK

  def getInfo(worldPos: Vec3d,
              surfaceNormal: Vec3d,
              incomingNormal: Vec3d,
              depth: Double,
              airRefractivityIndex: Double,
              context: Context): SurfaceInfo = {
    new SurfaceInfo(
      reflectance,
      getEmittance(
        worldPos,
        surfaceNormal,
        incomingNormal,
        depth,
        context),
      scatterFunction.outDirection(
        incomingNormal,
        surfaceNormal,
        airRefractivityIndex,
        context.random))
  }

}