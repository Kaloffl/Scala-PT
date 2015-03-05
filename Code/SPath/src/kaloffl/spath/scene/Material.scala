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
class Material(val emittance: Vec3d,
               val reflectance: Vec3d,
               val reflectivity: Float,
               val refractivity: Float,
               val refractivityIndex: Float,
               val glossiness: Float) {

  def reflectedNormal(surfaceNormal: Vec3d, incomingNormal: Vec3d, random: () ⇒ Float): Vec3d = {
    val randomHs = surfaceNormal.randomHemisphere(random)

    if (1.0f == glossiness) {
      return randomHs
    }

    val rnd = random() * (reflectivity + refractivity)
    if (rnd < refractivity) {
      val refracted = incomingNormal.refract(surfaceNormal, 1.0f, refractivityIndex)
      if (0.0f < glossiness) {
        val direction = refracted + randomHs * glossiness
        if (direction.dot(surfaceNormal) < 0) {
          return (refracted + randomHs.reflect(refracted) * glossiness).normalize
        }
        return direction.normalize
      }
      return refracted
    }

    val reflected = incomingNormal.reflect(surfaceNormal)
    if (0.0f < glossiness) {
      val direction = reflected + randomHs * glossiness
      if (direction.dot(surfaceNormal) < 0) {
        return (reflected + randomHs.reflect(reflected) * glossiness).normalize
      }
      return direction.normalize
    }
    return reflected
  }
}