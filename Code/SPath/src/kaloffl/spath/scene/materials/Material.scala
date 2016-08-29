package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Vec2d, Vec3d}

trait Material {
  def absorbtion: Color = Color.White
  def scatterProbability: Float = 0
  def emission: Color = Color.Black
  def ior: Float = 1.0f
  def getScattering(incomingNormal: Vec3d, surfaceNormal: Vec3d, uv: Vec2d, outsideIor: Float, random: DoubleSupplier): Array[Vec3d]
  def evaluateBSDF(toEye: Vec3d, surfaceNormal: Vec3d, toLight: Vec3d, uv: Vec2d, outsideIor: Float): Color
}

class Scattering(
                val weight: Float,
                val color: Color,
                val normal: Vec3d)

class DielectricMaterial(
                        val albedo: (Float, Float) => Color,
                        val roughness: (Float, Float) => Float = (_, _) => 0f,
                        override val ior: Float = 1.42f
                        ) extends Material {

  override def getScattering(
                              incomingNormal: Vec3d,
                              surfaceNormal: Vec3d,
                              uv: Vec2d,
                              outsideIor: Float,
                              random: DoubleSupplier): Array[Vec3d] = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.pow(roughness(u, v), 4).toFloat
    val axis = surfaceNormal.randomConeSample(Vec2d.random(random), rough, 0)
    val reflected = incomingNormal.reflect(axis)

    Array(
      if(reflected.dot(surfaceNormal) < 0) -reflected else reflected,
      surfaceNormal.randomHemisphere(Vec2d.random(random)))
  }

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float): Color = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val h = (toEye + toLight).normalize
    val rough = Math.pow(roughness(u, v), 4).toFloat
    if (h.dot(surfaceNormal) + 0.0001 < (1 - rough)) {
      // no specular because half-vector doesn't match surface roughness
      return albedo(u, v) * surfaceNormal.dot(toLight).toFloat
    } else {
      val r = (-toEye).refractance(h, outsideIor, ior).toFloat
      return Color.White * r + albedo(u, v) * (1 - r) * surfaceNormal.dot(toLight).toFloat
    }
  }
}

class MetalMaterial(
                   val color: (Float, Float) => Color,
                   val roughness: (Float, Float) => Float = (_, _) => 0f,
                   override val ior: Float = 2f
                   ) extends Material {

  override def getScattering(
                              incomingNormal: Vec3d,
                              surfaceNormal: Vec3d,
                              uv: Vec2d,
                              outsideIor: Float,
                              random: DoubleSupplier): Array[Vec3d] = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.pow(roughness(u, v), 4).toFloat
    val axis = surfaceNormal.randomConeSample(Vec2d.random(random), rough, 0)
    val reflected = incomingNormal.reflect(axis)

    Array(if (reflected.dot(surfaceNormal) < 0) -reflected else reflected)
  }

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float): Color = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val h = (toEye + toLight).normalize
    val r = (-toEye).refractance(h, outsideIor, ior).toFloat
    return Color.White * (1 - r) + color(u, v) * r
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

  override def getScattering(
                              incomingNormal: Vec3d,
                              surfaceNormal: Vec3d,
                              uv: Vec2d,
                              outsideIor: Float,
                              random: DoubleSupplier): Array[Vec3d] = {
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

    val r = incomingNormal.refractance(axis, ior1, ior2)

    if (ior1 == ior2) {
      Array(incomingNormal)
    } else if (r >= 0.99) {
      Array(incomingNormal.reflect(axis))
    } else if (r <= 0.01) {
      Array(incomingNormal.refract(axis, ior1, ior2))
    } else {
      Array(incomingNormal.reflect(axis), incomingNormal.refract(axis, ior1, ior2))
    }
  }

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float): Color = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.pow(roughness(u, v), 4).toFloat

    val eyeDir = toEye dot surfaceNormal
    val lightDir = toLight dot surfaceNormal

    var ior1 = outsideIor
    var ior2 = ior

    if (eyeDir < 0) {
      ior1 = ior
      ior2 = outsideIor
    }

    if (eyeDir * lightDir < 0) {
      // transmission
      val h = if (ior1 != ior2) {
        (-toEye - toLight * ior2 / ior1).normalize
      } else {
        surfaceNormal
      }
      if (h.dot(surfaceNormal) + 0.0001 < (1 - rough)) {
        // if the angle of refraction isn't possible with the
        // surface roughness we throw it away.
        return Color.Red
      } else {
        val r = (-toEye).refractance(h, ior1, ior2).toFloat
        return surfaceColor(u, v) * (1 - r)
      }

    } else {
      // reflection
      val h = (toEye + toLight).normalize
      if (h.dot(surfaceNormal) + 0.0001 < (1 - rough)) {
        // direction doesn't fit in reflection code, but wasn't transmitted
        // this is impossible with this type of material
        return Color.Black
      } else {
        val r = (-toEye).refractance(h, ior1, ior2).toFloat
        return Color.White * r
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
                              outsideIor: Float,
                              random: DoubleSupplier): Array[Vec3d] = Array()

  override def evaluateBSDF(
                             toEye: Vec3d,
                             surfaceNormal: Vec3d,
                             toLight: Vec3d,
                             uv: Vec2d,
                             outsideIor: Float): Color = ???
}

object DiffuseMaterial {
  def apply(color: Color) = new DielectricMaterial(albedo = (_, _) => color, roughness = (_, _) => 1)
}