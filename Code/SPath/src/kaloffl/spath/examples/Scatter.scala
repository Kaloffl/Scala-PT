package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.PinholeCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.Viewpoint
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer

object Scatter {
  def main(args: Array[String]): Unit = {

    val glassColor = Color(0.2f, 0.4f, 0.5f)
    val refraction = 2
    val matGlass = Array.tabulate(10) { i =>
      new TransparentMaterial(
        color = glassColor,
        absorbtionDepth = 2f / (1 << i),
        scatterProbability = 1,
        refractiveIndex = refraction)
    }

    val matRedDiffuse = DiffuseMaterial(Color(0.9f, 0.6f, 0.6f))
    val matGreenDiffuse = DiffuseMaterial(Color(0.6f, 0.9f, 0.6f))
    val matBlueDiffuse = DiffuseMaterial(Color(0.6f, 0.6f, 0.9f))
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = LightMaterial(Color.White * 2)

    val environment = Array(
      SceneNode(AABB(Vec3d(0, 16.5, 0), Vec3d(32, 1, 32)), matWhiteLight),

      SceneNode(AABB(Vec3d(0, -0.5, 0), Vec3d(32, 1, 32)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(16.5f, 8, 0), Vec3d(1, 16, 32)), matRedDiffuse),
      SceneNode(AABB(Vec3d(-16.5f, 8, 0), Vec3d(1, 16, 32)), matGreenDiffuse),
      SceneNode(AABB(Vec3d(0, 8, -16.5f), Vec3d(32, 16, 1)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(0, 8, 16.5), Vec3d(32, 16, 1)), matWhiteDiffuse))

    val minHeight = 1.0
    val maxHeight = 4.0
    val objects = (for (x ← 0 until 10; y ← 0 until 10) yield {
      val height = y * (maxHeight - minHeight) / 10.0 + minHeight
      SceneNode(
        AABB(Vec3d(x * 2 - 10, height / 2 + 0.01, y * 2 - 10), Vec3d(1, height, 1)),
        matGlass(x))
    }).toArray

    val front = Vec3d(0, -11, 9)
    val up = front.cross(Vec3d.Left).normalize

    RenderEngine.render(
      bounces = 12,
      target = new JfxDisplay(1280, 720),
      tracer = PathTracer,
      view = new Viewpoint(
        position = Vec3d(0, 14, -14),
        forward = front.normalize,
        up = up),
      scene = new Scene(
        root = SceneNode(environment ++ objects),
        camera = new PinholeCamera))
  }
}