package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.SurfaceInfo

class Material(
    val reflectance: Color, 
    val emittance: Color = Color.Black, 
    val scatterFunction: ScatterFunction) {

  def scatterProbability: Double = 0.0

  def refractiveIndex: Float = 1.0f
  
  def absorbtion = -reflectance

  def getInfo(incomingNormal: Vec3d,
              surfaceNormal: ⇒ Vec3d,
              textureCoordinate: ⇒ Vec2d,
              airRefractiveIndex: Float,
              random: DoubleSupplier): SurfaceInfo = {
    new SurfaceInfo(
      reflectance,
      scatterFunction.outDirections(
        incomingNormal,
        surfaceNormal,
        airRefractiveIndex,
        random))
  }

}

object DiffuseMaterial {
  def apply(color: Color) = new Material(color, Color.Black, DiffuseFunction)
}

object LightMaterial {
  def apply(emittance: Color): Material = {
    new Material(Color.Black, emittance, DummyFunction)
  }
}

object ReflectiveMaterial {

  def apply(color: Color) = new Material(color, Color.Black, ReflectFunction)

  def apply(color: Color, glossiness: Float) =
    new Material(color, Color.Black, new GlossyReflectFunction(glossiness))
}

object RefractiveMaterial {

  def apply(color: Color, refractiveIndex: Float, glossiness: Float = 0) =
    new Material(color, Color.Black, new RefractFunction(refractiveIndex, glossiness))

}