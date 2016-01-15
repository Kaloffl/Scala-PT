package kaloffl.spath.examples

import java.util.concurrent.ThreadLocalRandom
import java.util.function.DoubleSupplier

import kaloffl.spath.Display
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.DirectionalLightMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode

object Hemisphere {

  def main(args: Array[String]): Unit = {
    val rng = new DoubleSupplier() {
      override def getAsDouble(): Double = ThreadLocalRandom.current.nextDouble
    }

    val matAir = new TransparentMaterial(Color.White, 0.0, 0.0, 1.0)
    val matSky = new DirectionalLightMaterial(Color.White * 2, Vec3d(0, 1, -3).normalize, 0.5f)

    val diffuseMaterials = Array(
        DiffuseMaterial(Color(0.9f, 0.1f, 0.1f)),
        DiffuseMaterial(Color(0.1f, 0.9f, 0.1f)),
        DiffuseMaterial(Color(0.1f, 0.1f, 0.9f)),
        DiffuseMaterial(Color(0.9f, 0.9f, 0.1f)),
        DiffuseMaterial(Color(0.1f, 0.9f, 0.9f)),
        DiffuseMaterial(Color(0.9f, 0.1f, 0.9f)),
        DiffuseMaterial(Color(0.1f, 0.1f, 0.1f)),
        DiffuseMaterial(Color(0.9f, 0.9f, 0.9f)),
        DiffuseMaterial(Color(0.5f, 0.5f, 0.5f)))

    val hemisphere = SceneNode((for (i ← 0 to 2000) yield {
      val weight = Math.cos(rng.getAsDouble * Math.PI / 2)
      val rhs = Vec3d.Back.weightedHemisphere(Vec2d.random(rng))
      SceneNode(
        new Sphere(rhs, 0.025f),
        diffuseMaterials((rng.getAsDouble * diffuseMaterials.length).toInt))
    }).toArray)

    RenderEngine.render(
      bounces = 12,
      target = new Display(1280, 720),
      scene = new Scene(
        root = hemisphere,
        airMedium = matAir,
        skyMaterial = matSky,
        camera = new Camera(
          position = Vec3d(0, 0, 2.2),
          forward = Vec3d(0, 0, -1).normalize,
          up = Vec3d.Left)))
  }
}