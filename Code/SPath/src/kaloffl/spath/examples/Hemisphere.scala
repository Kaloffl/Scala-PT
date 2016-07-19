package kaloffl.spath.examples

import java.util.concurrent.ThreadLocalRandom
import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Vec2d, Vec3d}
import kaloffl.spath.scene.materials.{DiffuseMaterial, DirectionalSky}
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.{PinholeCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.PathTracer
import kaloffl.spath.{JfxDisplay, RenderEngine}

object Hemisphere {

  def main(args: Array[String]): Unit = {
    val rng = new DoubleSupplier() {
      override def getAsDouble: Double = ThreadLocalRandom.current.nextDouble
    }

    val matSky = new DirectionalSky(
      color = Color.White * 2,
      direction = Vec3d(0, 1, -3).normalize,
      limit = 0.5f)

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
      target = new JfxDisplay(1280, 720),
      tracer = PathTracer,
      view = new Viewpoint(
        position = Vec3d(0, 0, -2.2),
        forward = Vec3d(0, 0, 1).normalize,
        up = Vec3d.Left),
      scene = new Scene(
        root = hemisphere,
        skyMaterial = matSky,
        camera = new PinholeCamera))
  }
}