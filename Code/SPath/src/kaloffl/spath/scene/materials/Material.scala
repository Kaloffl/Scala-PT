package kaloffl.spath.scene.materials

import kaloffl.spath.math.{Color, Vec2d, Vec3d}
import kaloffl.spath.sampler.{DiffuseSampler, ReflectionSampler, RefractionSampler, Sampler}

trait Material {
  /**
    * @return Returns the amount of color that is absorbed after 1 meter in a medium.
    */
  def absorbtion: Color = Color.White

  /**
    * @return Distance at which a ray has a 50% chance of being scattered
    */
  def scatterProbability: Float = 0

  /**
    * @return Amount of light emitted by this material.
    */
  def emission: Color = Color.Black

  /**
    * @return Index of refraction of this material.
    */
  def ior: Float = 1.0f

  def getSamplers: Array[_ <: Sampler]

  /**
    * @param toEye Normal leading along the path to the camera
    * @param surfaceNormal Normal direction on the surface at the intersection point
    * @param toLight Normal leading along the path to the light
    * @param uv Texture coordinate at the intersection point
    * @param outsideIor Index of refraction of the medium outside of the hit object
    * @return Color of the light that can be transported between the given directions
    */
  def evaluateBSDF(
                    toEye: Vec3d,
                    surfaceNormal: Vec3d,
                    toLight: Vec3d,
                    uv: Vec2d,
                    outsideIor: Float
                  ): Color
}

class DielectricMaterial(
                        val albedo: (Float, Float) => Color,
                        val roughness: (Float, Float) => Float = (_, _) => 0f,
                        override val ior: Float = 1.42f
                        ) extends Material {

  val samplers = Array(DiffuseSampler, new ReflectionSampler(roughness))

  override def getSamplers: Array[_ <: Sampler] = samplers

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float
                           ): Color = {
    if (toLight.dot(surfaceNormal) < 0) {
      return Color.Black
    }

    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.max(0.0001f, Math.pow(roughness(u, v), 2).toFloat)
    val reflected = (-toEye).reflect(surfaceNormal)
    if (toLight.dot(reflected) < (1 - rough)) {
      // no specular because half-vector doesn't match surface roughness
      return albedo(u, v)
    } else {
      val h = (toEye + toLight).normalize
      val r = (-toEye).refractance(h, outsideIor, ior).toFloat
      val p = 1 / rough
      val c = albedo(u, v)
      return new Color(p * r + c.r2 * (1 - r),
                       p * r + c.g2 * (1 - r),
                       p * r + c.b2 * (1 - r))
    }
  }
}

class MetalMaterial(
                   val color: (Float, Float) => Color,
                   val roughness: (Float, Float) => Float = (_, _) => 0f,
                   override val ior: Float = 2f
                   ) extends Material {


  val samplers = Array(new ReflectionSampler(roughness))

  override def getSamplers: Array[_ <: Sampler] = samplers

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float): Color = {
    if (toLight.dot(surfaceNormal) < 0) {
      return Color.Black
    }

    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.max(0.0001f, Math.pow(roughness(u, v), 2).toFloat)
    val reflected = (-toEye).reflect(surfaceNormal)
    if (toLight.dot(reflected) < (1 - rough)) {
      return Color.Black
    } else {
      val h = (toEye + toLight).normalize
      val r = (-toEye).refractance(h, outsideIor, ior).toFloat
      val p = 1 / rough
      val c = color(u, v)
      return new Color(p * (r + c.r2 * (1 - r)),
                       p * (r + c.g2 * (1 - r)),
                       p * (r + c.b2 * (1 - r)))
    }
  }
}

class TransparentMaterial(
                         val surfaceColor: (Float, Float) => Color = (_, _) => Color.White,
                         val roughness: (Float, Float) => Float = (_, _) => 0f,
                         val volumeColor: Color = Color.Black,
                         val absorbtionDepth: Float = 1f,
                         override val scatterProbability: Float = 0f,
                         override val ior: Float = 1.0f) extends Material {

  override def absorbtion = volumeColor / absorbtionDepth

  val samplers = Array(new ReflectionSampler(roughness), new RefractionSampler(ior, roughness))

  override def getSamplers: Array[_ <: Sampler] = samplers

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float): Color = {

    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.max(0.0001f, Math.pow(roughness(u, v), 2).toFloat)

    if (outsideIor == ior) {
      if (toEye.dot(toLight) > -0.999) {
        Color.Black
      } else {
        return surfaceColor(u, v)
      }
    }

    var ior1 = outsideIor
    var ior2 = ior
    var norm = surfaceNormal

    val eyeDir = toEye.dot(surfaceNormal)
    val lightDir = toLight.dot(surfaceNormal)

    // swap IORs if we are leaving a medium instead of entering
    if (eyeDir < 0) {
      ior1 = ior
      ior2 = outsideIor
      norm = -surfaceNormal
    }

    val fromEye = -toEye

    if (eyeDir * lightDir < 0) {
      // transmission
      val refracted = fromEye.refract(norm, ior1, ior2)
      if (refracted.dot(toLight) < (1 - rough)) {
        return Color.Black
      } else {
        var h = -(toEye * ior1 + toLight * ior2).normalize
        if (h.dot(toEye) < 0) h = -h
        val r = fromEye.refractance(h, ior1, ior2).toFloat
        val p = 1 / rough
        return surfaceColor(u, v) * (1 - r) * p
      }
    } else {
      // reflection
      val reflected = fromEye.reflect(norm)
      if (reflected.dot(toLight) < (1 - rough)) {
        return Color.Black
      } else {
        val h = (toEye + toLight).normalize
        val r = fromEye.refractance(h, ior1, ior2).toFloat
        val p = 1 / rough
        return Color.White * r * p
      }
    }
  }
}

class EmittingMaterial(
                      val color: Color,
                      val intensity: Float) extends Material {

  override def emission = color * intensity

  override def getSamplers: Array[_ <: Sampler] = Array[Sampler]()

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float
                           ): Color = {
    Color.Black
  }
}

object DiffuseMaterial {
  def apply(color: Color) = new DielectricMaterial(albedo = (_, _) => color, roughness = (_, _) => 1)
}