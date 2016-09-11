package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Vec2d, Vec3d}

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
    * @return Index of refraction of this material, for red, green and blue frequency.
    */
  def ior: Color = Color.White // TODO: replace color with a different type

  /**
    * @param incomingNormal Normal of the ray hitting the surface
    * @param surfaceNormal Normal of the surface
    * @param uv Texture coordinate at the intersection point
    * @param outsideIor Index of refraction of the medium outside of the hit object
    * @param random Random number supplier
    * @return List of directions and probabilities of possible outgoing rays
    */
  def getScattering(
                     incomingNormal: Vec3d,
                     surfaceNormal: Vec3d,
                     uv: Vec2d,
                     outsideIor: Color,
                     random: DoubleSupplier
                   ): (Array[Vec3d], Array[Float])

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
                    outsideIor: Color
                  ): Color
}

class DielectricMaterial(
                        val albedo: (Float, Float) => Color,
                        val roughness: (Float, Float) => Float = (_, _) => 0f,
                        override val ior: Color = new Color(1.42f, 1.42f, 1.42f)
                        ) extends Material {

  override def getScattering(
                              incomingNormal: Vec3d,
                              surfaceNormal: Vec3d,
                              uv: Vec2d,
                              outsideIor: Color,
                              random: DoubleSupplier): (Array[Vec3d], Array[Float]) = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.pow(roughness(u, v), 4).toFloat
    // TODO make the microfacet distribution function a changable property of the material
    val axis = surfaceNormal.randomConeSample(Vec2d.random(random), rough, 0)
    val reflected = incomingNormal.reflect(axis)

    if (reflected.dot(surfaceNormal) < 0) {
      // If the reflected direction would penetrate the surface it just becomes diffuse scattering
      (Array(surfaceNormal.weightedHemisphere(Vec2d.random(random))), Array(1.0f))
    } else {
      val rR = incomingNormal.refractance(axis, outsideIor.r2, ior.r2).toFloat
      val rG = incomingNormal.refractance(axis, outsideIor.g2, ior.g2).toFloat
      val rB = incomingNormal.refractance(axis, outsideIor.b2, ior.b2).toFloat
      val rAvg = (rR + rB + rG) / 3
      (Array(reflected, surfaceNormal.weightedHemisphere(Vec2d.random(random))), Array(rAvg, 1.0f - rAvg))
    }
  }

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Color): Color = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val h = (toEye + toLight).normalize
    val rough = Math.pow(roughness(u, v), 4).toFloat
    if (h.dot(surfaceNormal) + 0.0001 < (1 - rough)) {
      // no specular because half-vector doesn't match surface roughness
      return albedo(u, v) * surfaceNormal.dot(toLight).toFloat
    } else {
      val rR = (-toEye).refractance(h, outsideIor.r2, ior.r2).toFloat
      val rG = (-toEye).refractance(h, outsideIor.g2, ior.g2).toFloat
      val rB = (-toEye).refractance(h, outsideIor.b2, ior.b2).toFloat
      val c = albedo(u, v)
      return new Color(rR + c.r2 * (1 - rR),
                       rG + c.g2 * (1 - rG),
                       rB + c.b2 * (1 - rB))
    }
  }
}

class MetalMaterial(
                   val color: (Float, Float) => Color,
                   val roughness: (Float, Float) => Float = (_, _) => 0f,
                   override val ior: Color = new Color(2f, 2f, 2f)
                   ) extends Material {

  override def getScattering(
                              incomingNormal: Vec3d,
                              surfaceNormal: Vec3d,
                              uv: Vec2d,
                              outsideIor: Color,
                              random: DoubleSupplier): (Array[Vec3d], Array[Float]) = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.pow(roughness(u, v), 4).toFloat
    val axis = surfaceNormal.randomConeSample(Vec2d.random(random), rough, 0)
    val reflected = incomingNormal.reflect(axis)

    // TODO find a better way to handle reflected directions that would penetrate the surface
    (Array(if (reflected.dot(surfaceNormal) < 0) -reflected else reflected), Array(1.0f))
  }

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Color): Color = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val h = (toEye + toLight).normalize
    val rough = Math.pow(roughness(u, v), 4).toFloat
    if (h.dot(surfaceNormal) + 0.0001 < (1 - rough)) {
      return Color.Black
    } else {
      val rR = (-toEye).refractance(h, outsideIor.r2, ior.r2).toFloat
      val rG = (-toEye).refractance(h, outsideIor.g2, ior.g2).toFloat
      val rB = (-toEye).refractance(h, outsideIor.b2, ior.b2).toFloat
      val c = color(u, v)
      return new Color(rR + c.r2 * (1 - rR),
                       rG + c.g2 * (1 - rG),
                       rB + c.b2 * (1 - rB))
    }
  }
}

class TransparentMaterial(
                         val surfaceColor: (Float, Float) => Color = (_, _) => Color.White,
                         val roughness: (Float, Float) => Float = (_, _) => 0f,
                         val volumeColor: Color = Color.Black,
                         val absorbtionDepth: Float = 1f,
                         override val scatterProbability: Float = 0f,
                         override val ior: Color = Color.White) extends Material {

  override def absorbtion = volumeColor / absorbtionDepth

  override def getScattering(
                              incomingNormal: Vec3d,
                              surfaceNormal: Vec3d,
                              uv: Vec2d,
                              outsideIor: Color,
                              random: DoubleSupplier): (Array[Vec3d], Array[Float]) = {
    if (ior == outsideIor) {
      return (Array(incomingNormal), Array(1))
    }

    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.pow(roughness(u, v), 4).toFloat

    var ior1 = outsideIor
    var ior2 = ior
    var norm = surfaceNormal

    if (surfaceNormal.dot(incomingNormal) > 0) {
      ior1 = ior
      ior2 = outsideIor
      norm = -surfaceNormal
    }
    val axis = norm.randomConeSample(Vec2d.random(random), rough, 0)

    val rR = incomingNormal.refractance(axis, ior1.r2, ior2.r2).toFloat
    val rG = incomingNormal.refractance(axis, ior1.g2, ior2.g2).toFloat
    val rB = incomingNormal.refractance(axis, ior1.b2, ior2.b2).toFloat
    if (rR == rG & rR == rB) {
      (
        Array(
          incomingNormal.reflect(axis),
          incomingNormal.refract(axis, ior1.r2, ior2.r2)),
        Array(rR, 1 - rR))
    } else {
      (
        Array(
          incomingNormal.reflect(axis),
          incomingNormal.refract(axis, ior1.r2, ior2.r2),
          incomingNormal.refract(axis, ior1.g2, ior2.g2),
          incomingNormal.refract(axis, ior1.b2, ior2.b2)),
        Array(
          (rR + rG + rB) / 3,
          (1 - rR) / 3,
          (1 - rG) / 3,
          (1 - rB) / 3))
    }
  }

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Color): Color = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.pow(roughness(u, v), 4).toFloat
    val rmin = 1 - rough - 0.00000001
    val fromEye = -toEye

    if (outsideIor == ior) {
      if (fromEye.dot(toLight) < rmin) {
        Color.Black
      } else {
        return surfaceColor(u, v)
      }
    }
    if (Math.abs(toEye.dot(surfaceNormal)) > rmin) {
      return surfaceColor(u, v)
    }

    val eyeDir = Math.signum(toEye dot surfaceNormal)
    val lightDir = Math.signum(toLight dot surfaceNormal)

    var ior1 = outsideIor
    var ior2 = ior
    var norm = surfaceNormal

    // swap IORs if we are leaving a medium instead of entering
    if (eyeDir < 0) {
      ior1 = ior
      ior2 = outsideIor
      norm = -surfaceNormal
    }

    if (eyeDir * lightDir < 0) {
      // transmission
      val hR = -(toEye * ior1.r2 + toLight * ior2.r2).normalize
      val hG = -(toEye * ior1.g2 + toLight * ior2.g2).normalize
      val hB = -(toEye * ior1.b2 + toLight * ior2.b2).normalize
      var scale = 4
      val rR = if (hR.dot(surfaceNormal) < rmin) {
        0
      } else {
        scale -= 1
        1 - fromEye.refractance(hR * eyeDir, ior1.r2, ior2.r2).toFloat
      }
      val rG = if (hG.dot(surfaceNormal) < rmin) {
        0
      } else {
        scale -= 1
        1 - fromEye.refractance(hG * eyeDir, ior1.g2, ior2.g2).toFloat
      }
      val rB = if (hB.dot(surfaceNormal) < rmin) {
        0
      } else {
        scale -= 1
        1 - fromEye.refractance(hB * eyeDir, ior1.b2, ior2.b2).toFloat
      }
      val sc = surfaceColor(u, v)
      return new Color(sc.r2 * rR * scale, sc.g2 * rG * scale, sc.b2 * rB * scale)
    } else {
      // reflection
      val h = (toEye + toLight).normalize
      if (h.dot(norm) < rmin) {
        return Color.Black
      } else {
        return new Color(fromEye.refractance(h, ior1.r2, ior2.r2).toFloat,
                         fromEye.refractance(h, ior1.g2, ior2.g2).toFloat,
                         fromEye.refractance(h, ior1.b2, ior2.b2).toFloat)
      }
    }
  }
}

class EmittingMaterial(
                      val color: Color,
                      val intensity: Float) extends Material {

  override def emission = color * intensity

  override def getScattering(
                              incomingNormal: Vec3d,
                              surfaceNormal: Vec3d,
                              uv: Vec2d,
                              outsideIor: Color,
                              random: DoubleSupplier): (Array[Vec3d], Array[Float]) = ???

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Color): Color = ???
}

object DiffuseMaterial {
  def apply(color: Color) = new DielectricMaterial(albedo = (_, _) => color, roughness = (_, _) => 1)
}