package kaloffl.spath.scene

import kaloffl.spath.math.Vec3d

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
  
  def maxEmittance: Vec3d = Vec3d.BLACK
  
  def reflectanceAt(worldPos: Vec3d, normal: Vec3d): Vec3d
  
  def reflectedNormal(surfaceNormal: Vec3d, incomingNormal: Vec3d, random: () ⇒ Float): Vec3d

}