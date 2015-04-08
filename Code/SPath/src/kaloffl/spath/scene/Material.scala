package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation

trait Material {

  def minEmittance: Color = Color.BLACK

  def emittanceAt(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    context: Context): Color = Color.BLACK

  def attenuation: Attenuation
    
  def getInfo(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    context: Context): SurfaceInfo

}