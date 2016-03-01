package kaloffl.spath.examples

import java.util.Random
import java.util.function.DoubleSupplier

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.PinholeCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer

object Scatter2 {
  def main(args: Array[String]): Unit = {
    val rng = new DoubleSupplier() {
      val random = new Random(123467)
      override def getAsDouble(): Double = random.nextDouble
    }

    val glassColor = Color(0.2f, 0.4f, 0.5f)

    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matWhiteLight = LightMaterial(Color.White * 2)

    val environment = Array(
      SceneNode(AABB(Vec3d(0, 16.5, 0), Vec3d(32, 1, 32)), matWhiteLight),
      SceneNode(AABB(Vec3d(0, -0.5, 0), Vec3d(32, 1, 32)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(16.5f, 8, 0), Vec3d(1, 16, 32)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(-16.5f, 8, 0), Vec3d(1, 16, 32)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(0, 8, -16.5f), Vec3d(32, 16, 1)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(0, 8, 16.5), Vec3d(32, 16, 1)), matWhiteDiffuse))

    val minHeight = 1.0
    val maxHeight = 4.0
    val objects = (for (x ← 0 until 10; y ← 0 until 10) yield {
      val rnd = Vec2d(rng.getAsDouble, rng.getAsDouble)
      SceneNode(
        AABB(Vec3d(x * 2 - 9.5, 0.501, y * 2 - 9.5), Vec3d(1, 0.1, 1)),
        new TransparentMaterial(
          color = Color.randomColor(rnd, 0.5f),
          absorbtionDepth = 0.1f / (x * x + 1),
          scatterProbability = y * x + 4,
          refractiveIndex = 1.1f))
    }).toArray

    val front = Vec3d(0, -11, 9)
    val up = front.cross(Vec3d.Left).normalize

    RenderEngine.render(
      bounces = 12,
      target = new JfxDisplay(1280, 720),
      tracer = PathTracer,
      scene = new Scene(
        root = SceneNode(environment ++ objects),
        camera = new PinholeCamera(
          position = Vec3d(0, 14, -14),
          forward = front.normalize,
          up = up)))
  }
}