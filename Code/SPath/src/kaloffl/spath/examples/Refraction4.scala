package kaloffl.spath.examples

import java.util.concurrent.ThreadLocalRandom
import java.util.function.DoubleSupplier

import kaloffl.spath.Display
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode

object Refraction4 {
  def main(args: Array[String]): Unit = {
    val rng = new DoubleSupplier() {
      override def getAsDouble(): Double = ThreadLocalRandom.current.nextDouble
    }

    val display = new Display(1280, 720)

    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = new LightMaterial(Color.WHITE * 2, Attenuation.none)

    val matAir = new TransparentMaterial(Color.WHITE, 0, 0.0, 1.0)

    val environment = Array(
      SceneNode(
        AABB(Vec3d(0, 16.5, 0), Vec3d(32, 1, 32)),
        matWhiteLight),

      SceneNode(
        AABB(Vec3d(0, -0.5, 0), Vec3d(32, 1, 32)),
        matWhiteDiffuse),
      SceneNode(
        AABB(Vec3d(16.5f, 8, 0), Vec3d(1, 16, 32)),
        matWhiteDiffuse),
      SceneNode(
        AABB(Vec3d(-16.5f, 8, 0), Vec3d(1, 16, 32)),
        matWhiteDiffuse),
      SceneNode(
        AABB(Vec3d(0, 8, -16.5f), Vec3d(32, 16, 1)),
        matWhiteDiffuse),
      SceneNode(
        AABB(Vec3d(0, 8, 16.5), Vec3d(32, 16, 1)),
        matWhiteDiffuse))

    val minHeight = 1.0
    val maxHeight = 4.0
    val objects = (for (x ← 0 until 20; y ← 0 until 20) yield {
      SceneNode(
        new Sphere(Vec3d(x * 1.1 - 10.5, 0.501, y * 1.1 - 10.5), 0.5f),
        new TransparentMaterial(Color.randomColor(Vec2d.random(rng), 0.5f), 8, 0.0, 1.8))
    }).toArray

    val front = Vec3d(0, -11, 9)
    val up = front.cross(Vec3d.LEFT).normalize
    val camera = new Camera(Vec3d(0, 14, -14), front.normalize, up, 0.0, 3)

    val glassScene = new Scene(
        root = SceneNode(environment ++ objects), 
        camera = camera, 
        airMedium = matAir, 
        skyMaterial = matBlackDiffuse)

    RenderEngine.render(target = display, scene = glassScene, bounces = 12)
  }
}