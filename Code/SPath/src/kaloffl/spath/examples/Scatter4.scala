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
import kaloffl.spath.scene.materials.UniformSky
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer

object Scatter4 {
  def main(args: Array[String]): Unit = {

    val matPaper = new TransparentMaterial(
      color = Color(0.1f, 0.1f, 0.1f),
      scatterProbability = 500,
      refractiveIndex = 1.557f,
      glossiness = 0.01f)
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matRedLight = LightMaterial(Color.Red * 256)
    val matGreenLight = LightMaterial(Color.Green * 16)

    val glassTest = SceneNode(Array(
      SceneNode(AABB(Vec3d(0, 4.5, 0), Vec3d(10, 3, 0.001)), matPaper),
      SceneNode(AABB(Vec3d(0, 1.5, 0), Vec3d(10, 3, 0.001)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(-3, 3, -2), Vec3d(1, 1, 0.1)), matRedLight),
      SceneNode(AABB(Vec3d(3, 3, 2), Vec3d(1, 1, 0.1)), matGreenLight),
      SceneNode(AABB(Vec3d(0, -0.5, 0), Vec3d(10, 1, 10)), matBlackDiffuse)))

    val front = Vec3d(0, 0, -9).normalize

    RenderEngine.render(
      bounces = 128,
      target = new JfxDisplay(1280, 720),
      tracer = PathTracer,
      view = new Viewpoint(
        position = Vec3d(0, 3, 9),
        forward = front,
        up = Vec3d.Up),
      scene = new Scene(
        root = glassTest,
        skyMaterial = new UniformSky(Color.White),
        camera = new PinholeCamera))
  }
}