package kaloffl.spath.scene.materials

import kaloffl.spath.math.Vec3d

/**
 * An Object implementing the Scattering trait represents a function from an
 * incoming ray to one or more outgoing rays. Each outgoing ray has a weight
 * and all weights should add up to 1.<br>
 * Callers of this trait can choose how to deal with the number of outgoing
 * normals. They might choose to follow all of them and combine the results, or
 * they might choose a random one according to their weight.<br>
 * The calculation of the normal might be done lazy, so callers should only
 * call getNormal once per index and cache the result.
 */
trait Scattering {
  def paths: Int
  def getWeight(i: Int): Float
  def getNormal(i: Int): Vec3d
}

object TerminatedScattering extends Scattering {
  override def paths = 0
  override def getWeight(i: Int): Float = ???
  override def getNormal(i: Int): Vec3d = ???
}

class SingleRayScattering(outgoing: Vec3d) extends Scattering {
  override def paths = 1
  override def getWeight(i: Int): Float = 1f
  override def getNormal(i: Int): Vec3d = outgoing
}

class RefractiveScattering(incomingNormal: Vec3d,
                           surfaceNormal: Vec3d,
                           refractIndex1: Float,
                           refractIndex2: Float) extends Scattering {

  val weight = incomingNormal.refractance(surfaceNormal, refractIndex1, refractIndex2).toFloat
  override def paths = 2
  override def getWeight(i: Int): Float = {
    if (0 == i) weight else 1 - weight
  }
  override def getNormal(i: Int): Vec3d = {
    if (0 == i) {
      incomingNormal.reflect(surfaceNormal)
    } else {
      incomingNormal.refract(surfaceNormal, refractIndex1, refractIndex2)
    }
  }
}