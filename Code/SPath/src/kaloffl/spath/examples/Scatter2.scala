package kaloffl.spath.examples

import kaloffl.spath.Display
import kaloffl.spath.PathTracer
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.CheckeredMask
import kaloffl.spath.scene.DiffuseMaterial
import kaloffl.spath.scene.LightMaterial
import kaloffl.spath.scene.MaskedMaterial
import kaloffl.spath.scene.RefractiveMaterial
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.GridMask
import kaloffl.spath.scene.DirectionalLightMaterial
import java.util.function.DoubleSupplier
import java.util.concurrent.ThreadLocalRandom

object Scatter2 {
  def main(args: Array[String]): Unit = {
    val rng = new DoubleSupplier() {
      override def getAsDouble(): Double = ThreadLocalRandom.current.nextDouble
    }
    
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val glassColor = Color(0.2f, 0.4f, 0.5f)

    val matRedDiffuse = new DiffuseMaterial(Color(0.9f, 0.6f, 0.6f))
    val matGreenDiffuse = new DiffuseMaterial(Color(0.6f, 0.9f, 0.6f))
    val matBlueDiffuse = new DiffuseMaterial(Color(0.6f, 0.6f, 0.9f))
    val matBlackDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = new LightMaterial(Color.WHITE, 2, 1024)

    val mask = new GridMask(2, 0.04, Vec3d(0.5, 0.5, 0.5))
    val matBlackWhiteCheckered = new MaskedMaterial(matWhiteDiffuse, matBlueDiffuse, mask)

    val matAir = new TransparentMaterial(Color.WHITE, 0, 0.0, 1.0)

    val environment = Array(
      new SceneObject(
        AABB(Vec3d(0, 16.5, 0), Vec3d(32, 1, 32)),
        matWhiteLight),

      new SceneObject(
        AABB(Vec3d(0, -0.5, 0), Vec3d(32, 1, 32)),
        matWhiteDiffuse), //matBlackWhiteCheckered),
      new SceneObject(
        AABB(Vec3d(16.5f, 8, 0), Vec3d(1, 16, 32)),
        matWhiteDiffuse), //matRedDiffuse),
      new SceneObject(
        AABB(Vec3d(-16.5f, 8, 0), Vec3d(1, 16, 32)),
        matWhiteDiffuse), //matGreenDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 8, -16.5f), Vec3d(32, 16, 1)),
        matWhiteDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 8, 16.5), Vec3d(32, 16, 1)),
        matWhiteDiffuse))

    val minHeight = 1.0
    val maxHeight = 4.0
    val objects = (for (x ← 0 until 10; y ← 0 until 10) yield {
      new SceneObject(
        AABB(Vec3d(x * 2 - 9.5, 0.501, y * 2 - 9.5), Vec3d(1)),
        new TransparentMaterial(Color.randomColor(rng, 0.5f), Math.pow(2, x % 5), 0.2 * y * x / 2, 1.1))
    }).toArray

    val front = Vec3d(0, -11, 9)
    val up = front.cross(Vec3d.LEFT).normalize
    val camera = new Camera(Vec3d(0, 14, -14), front.normalize, up, 0.0, 3)

    val glassScene = new Scene(environment ++ objects, camera, matAir, matBlackDiffuse)

    pathTracer.render(display, glassScene, bounces = 12)
  }
}