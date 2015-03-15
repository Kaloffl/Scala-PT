package kaloffl.spath.scene

import kaloffl.spath.math.Vec3d

class LightMaterial(val color: Vec3d) extends Material {

  override def terminatesPath: Boolean = true
  
  override def maxEmittance = color
  
  override def reflectanceAt(worldPos: Vec3d, normal: Vec3d): Vec3d = {
    return color
  }
  
  override def reflectedNormal(
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    random: () ⇒ Float): Vec3d = {

    return surfaceNormal.randomHemisphere(random)
  }
}