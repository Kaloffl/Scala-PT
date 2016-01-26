package kaloffl.spath.scene.materials

import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Vec2d

/**
 * An Object implementing the Scattering trait represents a function from an
 * incoming ray to one or more outgoing rays. Each outgoing ray has a weight
 * and all weights should add up to 1. <br>
 * Callers can also check how much a ray going in a certain direction would
 * contribute by calling the getContribution method. With a diffuse scattering
 * the contribution weakens as the angle to the surface normal grows, on a
 * reflective surface all but one directions might contribute nothing.<br>
 * Note that the directions gotten from a scattering object don't need to be
 * weighted by the getContribution result because the should already be sampled
 * with the contribution in mind.<br>
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
  def getContribution(normal: Vec3d): Float
}

object TerminatedScattering extends Scattering {
  override def paths = 0
  override def getWeight(i: Int): Float = ???
  override def getNormal(i: Int): Vec3d = ???
  override def getContribution(normal: Vec3d) = ???
}

class DiffuseScattering(surfaceNormal: Vec3d, random: Vec2d) extends Scattering {
  override def paths = 1
  override def getWeight(i: Int): Float = 1f
  override def getNormal(i: Int): Vec3d = surfaceNormal.weightedHemisphere(random)
  override def getContribution(normal: Vec3d) = Math.max(0, surfaceNormal.dot(normal).toFloat)
}

class SingleRayScattering(normal: Vec3d) extends Scattering {

  override def paths = 1
  override def getWeight(i: Int): Float = 1f
  override def getNormal(i: Int): Vec3d = normal
  override def getContribution(normal: Vec3d): Float = 0
}

class ReflectiveScattering(incomingNormal: Vec3d,
                           surfaceNormal: Vec3d,
                           glossiness: Float,
                           random: Vec2d) extends Scattering {

  val axis = surfaceNormal.randomConeSample(random, glossiness, 0)
  override def paths = 1
  override def getWeight(i: Int): Float = 1f
  override def getNormal(i: Int): Vec3d = incomingNormal.reflect(axis)
  override def getContribution(normal: Vec3d): Float = {
    val dot = surfaceNormal.dot(normal).toFloat
    if (dot >= 1 - glossiness) dot else 0
  }
}

class RefractiveScattering(incomingNormal: Vec3d,
                           surfaceNormal: Vec3d,
                           refractIndex1: Float,
                           refractIndex2: Float,
                           glossiness: Float,
                           random: Vec2d) extends Scattering {

  val axis = surfaceNormal.randomConeSample(random, glossiness, 0)
  val weight = incomingNormal.refractance(axis, refractIndex1, refractIndex2).toFloat
  override def paths = 2

  override def getWeight(i: Int): Float = {
    if (0 == i) weight else 1 - weight
  }

  override def getNormal(i: Int): Vec3d = {
    if (0 == i) {
      incomingNormal.reflect(axis)
    } else {
      incomingNormal.refract(axis, refractIndex1, refractIndex2)
    }
  }

  override def getContribution(normal: Vec3d): Float = {
    val dot = surfaceNormal.dot(normal).toFloat
    if (dot >= 1 - glossiness) dot * (1 - weight) else 0
  }
}