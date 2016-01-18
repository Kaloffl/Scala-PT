package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation
import kaloffl.spath.scene.SurfaceInfo
import kaloffl.spath.math.Vec2d

class Material(reflectance: Color, scatterFunction: ScatterFunction) {

  def scatterPropability: Double = 0.0

  def refractiveIndex: Double = 1.0

  def getAbsorbtion(worldPos: Vec3d,
                    context: Context): Color = Color.Black

  def getEmittance(worldPos: Vec3d,
                   surfaceNormal: Vec3d,
                   incomingNormal: Vec3d,
                   context: Context): Color = Color.Black

  def getInfo(incomingNormal: Vec3d,
              worldPos: ⇒ Vec3d,
              surfaceNormal: ⇒ Vec3d,
              textureCoordinate: ⇒ Vec2d,
              airRefractivityIndex: Double,
              context: Context): SurfaceInfo = {
    new SurfaceInfo(
      reflectance,
      getEmittance(
        worldPos,
        surfaceNormal,
        incomingNormal,
        context),
      scatterFunction.outDirection(
        incomingNormal,
        surfaceNormal,
        airRefractivityIndex,
        context.random))
  }

}