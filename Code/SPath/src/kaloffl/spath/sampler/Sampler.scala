package kaloffl.spath.sampler

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Ray, Vec2d, Vec3d}
import kaloffl.spath.scene.shapes.{Sphere, Triangle}

/**
  * Samplers are used to help with the integration of the integral  at
  * the heart of the rendering equation. Ideally, the renderer would
  * evaluate an infinite amount of evenly distributed rays over a surface,
  * but that is obviously not possible. These Samplers can guide the renderer
  * into the most valuable directions and also provide the factors needed to
  * make the integration unbiassed.
  * Not all Samplers are equally usefull everywhere. Usually the Samplers
  * are provided by a material that knows which Samplers match its BSDF
  * the closest. Other Samplers can be placed in the scene to show the
  * direction to lightsources.
  * When combining Samplers you have to use "multiple importance sampling"
  * to combine them. It is not sufficient to just average the results.
  */
trait Sampler {

  /**
    * @param position World position from where this method is called
    * @param incomingNormal Direction of the incoming ray
    * @param surfaceNormal Direction of the surface
    * @param uv Texture coordinate of the surface
    * @param outsideIor Index of refraction of the current medium
    * @param random Randum number generator
    * @return A direction
    */
  def getDirection(
                    position: Vec3d,
                    incomingNormal: Vec3d,
                    surfaceNormal: Vec3d,
                    uv: Vec2d,
                    outsideIor: Float,
                    random: DoubleSupplier
                  ): Vec3d

  /**
    * @param position World position from where this method is called
    * @param incomingNormal Direction of the incoming ray
    * @param surfaceNormal Direction of the surface
    * @param outgoingNormal Direction of which the probability is to be
    *                       determined
    * @param uv Texture coordinate of the surface
    * @param outsideIor Index of refraction of the current medium
    * @return The probability with which this sampler would have choosen
    *         the outgoingNormal parameter as a direction
    */
  def getPropability(
                      position: Vec3d,
                      incomingNormal: Vec3d,
                      surfaceNormal: Vec3d,
                      outgoingNormal: Vec3d,
                      uv: Vec2d,
                      outsideIor: Float
                    ): Float
}

/**
  * This Sampler does what is commonly known as "Importance Sampling"
  * (not MIS) for diffuse surfaces. Because light at grazing angles
  * contributes less, it focusses the renderer to directions near the
  * surface normal.
  */
object DiffuseSampler extends Sampler {
  override def getDirection(
                             position: Vec3d,
                             incomingNormal: Vec3d,
                             surfaceNormal: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float,
                             random: DoubleSupplier
                           ): Vec3d = {

    surfaceNormal.weightedHemisphere(Vec2d.random(random))
  }

  override def getPropability(
                               position: Vec3d,
                               incomingNormal: Vec3d,
                               surfaceNormal: Vec3d,
                               outgoingNormal: Vec3d,
                               uv: Vec2d,
                               outsideIor: Float
                             ): Float = {

    Math.max(0, 2 * surfaceNormal.dot(outgoingNormal).toFloat)
  }
}

/**
  * This Sampler focusses its directions in a cone along the reflection
  * of the incoming normal on the surface normal. The higher the roughness
  * on the surface, the wider the cone will get.
  *
  * @param roughness Provides the roughness of the surface
  */
class ReflectionSampler(val roughness: (Float, Float) => Float) extends Sampler {

  override def getDirection(
                             position: Vec3d,
                             incomingNormal: Vec3d,
                             surfaceNormal: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float,
                             random: DoubleSupplier
                           ): Vec3d = {

    val rough = Math.max(0.0001f, Math.pow(roughness(uv.x.toFloat, uv.y.toFloat), 2))
    val reflected = incomingNormal.reflect(surfaceNormal)
    val glossy = reflected.randomConeSample(Vec2d.random(random), rough, 0)
    if (glossy.dot(surfaceNormal) < 0) {
      return (-glossy).reflect(reflected)
    } else {
      return glossy
    }
  }

  override def getPropability(
                               position: Vec3d,
                               incomingNormal: Vec3d,
                               surfaceNormal: Vec3d,
                               outgoingNormal: Vec3d,
                               uv: Vec2d,
                               outsideIor: Float
                             ): Float = {

    val rough = Math.max(0.0001, Math.pow(roughness(uv.x.toFloat, uv.y.toFloat), 2)).toFloat
    val reflected = incomingNormal.reflect(surfaceNormal)
    if (reflected.dot(outgoingNormal) < (1 - rough)) {
      return 0.0f
    } else {
      return 1.0f / rough
    }
  }
}

/**
  * This Sampler focusses its directions in a cone along the refraction
  * of the incoming normal on the surface normal. The higher the roughness
  * on the surface, the wider the cone will get. The refraction also depends
  * on the indices of refraction on either side of the surface.
  *
  * @param ior Index of refraction below the surface
  * @param roughness Provides the roughness of the surface
  */
class RefractionSampler(val ior: Float, val roughness: (Float, Float) => Float) extends Sampler {

  override def getDirection(
                             position: Vec3d,
                             incomingNormal: Vec3d,
                             surfaceNormal: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float,
                             random: DoubleSupplier
                           ): Vec3d = {

    if (ior == outsideIor) {
      return incomingNormal
    }

    var ior1 = outsideIor
    var ior2 = ior
    var norm = surfaceNormal

    if (incomingNormal.dot(surfaceNormal) > 0) {
      ior1 = ior
      ior2 = outsideIor
      norm = -surfaceNormal
    }

    val rough = Math.max(0.0001f, Math.pow(roughness(uv.x.toFloat, uv.y.toFloat), 2))
    val refracted = incomingNormal.refract(norm, ior1, ior2)
    val glossy = refracted.randomConeSample(Vec2d.random(random), rough, 0)
    if (glossy.dot(norm) > 0) {
      return (-glossy).reflect(refracted)
    } else {
      return glossy
    }
  }

  override def getPropability(
                               position: Vec3d,
                               incomingNormal: Vec3d,
                               surfaceNormal: Vec3d,
                               outgoingNormal: Vec3d,
                               uv: Vec2d,
                               outsideIor: Float
                             ): Float = {

    if (ior == outsideIor) {
      if (incomingNormal.dot(outgoingNormal) > 0.999) {
        return 1.0f
      } else {
        return 0.0f
      }
    }

    var ior1 = outsideIor
    var ior2 = ior
    var norm = surfaceNormal

    if (incomingNormal.dot(surfaceNormal) > 0) {
      ior1 = ior
      ior2 = outsideIor
      norm = -surfaceNormal
    }

    val rough = Math.max(0.0001f, Math.pow(roughness(uv.x.toFloat, uv.y.toFloat), 2).toFloat)
    val refracted = incomingNormal.refract(norm, ior1, ior2)
    if (refracted.dot(outgoingNormal) < (1 - rough)) {
      return 0.0f
    } else {
      return 1.0f / rough
    }
  }
}

/**
  * This sampler
  * @param sphere
  */
class SphereSampler(sphere: Sphere) extends Sampler {

  def getSolidAngle(point: Vec3d): Double = {
    val dsq = (sphere.position - point).lengthSq
    return Math.sqrt(1 - sphere.radiusSq / dsq)
  }

  override def getDirection(
                             position: Vec3d,
                             incomingNormal: Vec3d,
                             surfaceNormal: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float,
                             random: DoubleSupplier
                           ): Vec3d = {

    val direction = (sphere.position - position).normalize
    return direction.randomConeSample(Vec2d.random(random), getSolidAngle(position), 0)
  }

  override def getPropability(
                               position: Vec3d,
                               incomingNormal: Vec3d,
                               surfaceNormal: Vec3d,
                               outgoingNormal: Vec3d,
                               uv: Vec2d,
                               outsideIor: Float
                             ): Float = {

    val dir = (sphere.position - position).normalize
    val angle = getSolidAngle(position)
    if (dir.dot(outgoingNormal) < (1 - angle)) {
      return 0.0f
    } else {
      return 1.0f / angle.toFloat
    }
  }
}

/**
  * Not tested, might be completely broken.
  */
class TriangleSampler(triangle: Triangle) extends Sampler {

  // TODO put this class to use in a test scene

  def getSolidAngle(point: Vec3d): Double = {
    val a = triangle.vertA - point
    val b = a + triangle.edgeA
    val c = a + triangle.edgeB
    val na = c cross b
    val nb = a cross c
    val nc = b cross a
    val la = na.length
    val lb = nb.length
    val lc = nc.length
    val aa = Math.acos((nb dot nc) / (lb * lc))
    val ab = Math.acos((nc dot na) / (lc * la))
    val ac = Math.acos((na dot nb) / (la * lb))
    return 1 - (aa + ab + ac) / (Math.PI * 2)
  }

  override def getDirection(
                             position: Vec3d,
                             incomingNormal: Vec3d,
                             surfaceNormal: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float,
                             random: DoubleSupplier
                           ): Vec3d = {

    val r1 = random.getAsDouble
    val t = random.getAsDouble
    val r2 = if (r1 + t > 1) 1 - t else t
    return (triangle.vertA - position + triangle.edgeA * r1 + triangle.edgeB * r2).normalize
  }

  override def getPropability(
                               position: Vec3d,
                               incomingNormal: Vec3d,
                               surfaceNormal: Vec3d,
                               outgoingNormal: Vec3d,
                               uv: Vec2d,
                               outsideIor: Float
                             ): Float = {

    val angle = getSolidAngle(position)
    val testRay = new Ray(position, outgoingNormal)
    if (triangle.getIntersectionDepth(testRay).isInfinite) {
      return 0.0f
    } else {
      return 1.0f / angle.toFloat
    }
  }
}