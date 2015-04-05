package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context

/**
 * The Material of an object in the scene.
 *
 * @param emittance If the material emits light, this value is a vector other than the origin vector.
 * @param reflectance The color of the surface
 * @param reflectivity How many rays are reflected instead of randomly bounced
 * @param refractivity How many rays are refracted through the object
 * @param refractivityIndex how distorted will the refracted rays be
 * @param glossiness How much randomness is used for reflected and refracted rays
 */
trait Material {

  def terminatesPath: Boolean = false

  def maxEmittance: Color = Color.BLACK

  def reflectanceAt(worldPos: Vec3d, normal: Vec3d, context: Context): Color

  def reflectNormal(
      worldPos: Vec3d,
      surfaceNormal: Vec3d, 
      incomingNormal: Vec3d, 
      context: Context): Vec3d

}