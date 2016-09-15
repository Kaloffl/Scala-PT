package kaloffl.spath.examples

import java.util.Random
import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Vec2d, Vec3d}
import kaloffl.spath.scene.materials.{DiffuseMaterial, EmittingMaterial, TransparentMaterial}
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.{PinholeCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.PathTracer
import kaloffl.spath.{JfxDisplay, RenderEngine}

object Scatter2 {
  def main(args: Array[String]): Unit = {
    val rng = new DoubleSupplier() {
      val random = new Random(123467)
      override def getAsDouble: Double = random.nextDouble
    }

    val glassColor = Color(0.2f, 0.4f, 0.5f)

    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matWhiteLight = new EmittingMaterial(Color.White, 2)

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
      val rnd = Vec2d(rng.getAsDouble(), rng.getAsDouble())
      SceneNode(
        AABB(Vec3d(x * 2 - 9.5, 0.501, y * 2 - 9.5), Vec3d(1, 0.1, 1)),
        new TransparentMaterial(
          volumeColor = Color.randomColor(rnd, 0.5f),
          absorbtionDepth = 0.1f / (x * x + 1),
          scatterProbability = y * x + 4,
          ior = 1.1f))
    }).toArray

    val front = Vec3d(0, -11, 9)
    val up = front.cross(Vec3d.Left).normalize

    RenderEngine.render(
      target = new JfxDisplay(1280, 720),
      tracer = new PathTracer(maxBounces = 12),
      view = new Viewpoint(
        position = Vec3d(0, 14, -14),
        forward = front.normalize,
        up = up),
      scene = new Scene(
        root = SceneNode(environment ++ objects),
        camera = new PinholeCamera))
  }
}