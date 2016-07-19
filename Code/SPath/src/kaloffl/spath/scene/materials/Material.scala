package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Vec2d, Vec3d}

trait Material {
  def absorbtion: Color = Color.White
  def scatterProbability: Float = 0
  def emission: Color = Color.Black
  def ior: Float = 1.0f
  def getScattering(incomingNormal: Vec3d, surfaceNormal: Vec3d, uv: Vec2d, outsideIor: Float, random: DoubleSupplier): Array[Scattering]
}

class Scattering(
                val weight: Float,
                val color: Color,
                val normal: Vec3d)

class DielectricMaterial(
                        val albedo: (Float, Float) => Color,
                        val roughness: (Float, Float) => Float = (_, _) => 0f,
                        val reflectivity: (Float, Float) => Float = (_, _) => 0f,
                        override val ior: Float = 1.42f
                        ) extends Material {

  override def getScattering(
                              incomingNormal: Vec3d,
                              surfaceNormal: Vec3d,
                              uv: Vec2d,
                              outsideIor: Float,
                              random: DoubleSupplier): Array[Scattering] = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.pow(roughness(u, v), 4).toFloat
    val axis = surfaceNormal.randomConeSample(Vec2d.random(random), rough, 0)
    val reflected = incomingNormal.reflect(axis)
    val r = incomingNormal.refractance(surfaceNormal, outsideIor, ior).toFloat
    val f0 = reflectivity(u, v)
    val fresnel = (f0 + (1 - f0) * r) * (1 - rough)

    Array(
      new Scattering(
        weight = fresnel,
        color = Color.White,
        normal = if(reflected.dot(surfaceNormal) < 0) -reflected else reflected),
      new Scattering(
        weight = 1 - fresnel,
        color = albedo(u, v),
        normal = surfaceNormal.weightedHemisphere(Vec2d.random(random))))
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
                              random: DoubleSupplier): Array[Scattering] = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.pow(roughness(u, v), 4).toFloat
    val axis = surfaceNormal.randomConeSample(Vec2d.random(random), rough, 0)
    val reflected = incomingNormal.reflect(axis)
    val normal = if (reflected.dot(surfaceNormal) < 0) -reflected else reflected
    val r = incomingNormal.refractance(surfaceNormal, outsideIor, ior).toFloat
    val fresnel = r * (1 - rough)
    val tint = color(u, v) * (1 - fresnel) + Color.White * fresnel

    Array(
      new Scattering(
        weight = 1,
        color = tint,
        normal = normal))
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
                              random: DoubleSupplier): Array[Scattering] = {
    val u = uv.x.toFloat
    val v = uv.y.toFloat
    val rough = Math.pow(roughness(u, v), 4).toFloat

    var ior1 = outsideIor
    var ior2 = ior
    var axis = surfaceNormal.randomConeSample(Vec2d.random(random), rough, 0)

    if (surfaceNormal.dot(incomingNormal) > 0) {
      ior1 = ior
      ior2 = outsideIor
      axis = -axis
    }

    val r = incomingNormal.refractance(axis, ior1, ior2).toFloat * (1 - rough)

    Array(
      new Scattering(
        weight = r,
        color = Color.White,
        normal = incomingNormal.reflect(axis)),
      new Scattering(
        weight = 1 - r,
        color = surfaceColor(u, v),
        normal = incomingNormal.refract(axis, ior1, ior2)))
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
                              random: DoubleSupplier): Array[Scattering] = Array()
}

object DiffuseMaterial {
  def apply(color: Color) = new DielectricMaterial(albedo = (_, _) => color, roughness = (_, _) => 1)
}