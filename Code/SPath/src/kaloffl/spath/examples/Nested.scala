package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.PinholeCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.materials.UniformSky
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer

object Nested {

  def main(args: Array[String]): Unit = {

    val matGlassRed = new TransparentMaterial(
      color = Color(0.1f, 0.9f, 0.9f),
      absorbtionDepth = 0.75f)
    val matGlassGreen = new TransparentMaterial(
      color = Color(0.9f, 0.1f, 0.9f),
      absorbtionDepth = 0.5f)
    val matGlassBlue = new TransparentMaterial(
      color = Color(0.9f, 0.9f, 0.1f),
      absorbtionDepth = 0.25f)
    val matFloor = DiffuseMaterial(Color(0.6f, 0.6f, 0.6f))

    val boxRed = SceneNode(AABB(Vec3d(0, 0, 0), Vec3d(2, 2, 3)), matGlassRed)
    val boxGreen = SceneNode(AABB(Vec3d(0, 0, 0), Vec3d(3, 1, 2)), matGlassGreen)
    val boxBlue = SceneNode(AABB(Vec3d(0, 0, 0), Vec3d(1, 3, 1)), matGlassBlue)

    RenderEngine.render(
      bounces = 12,
      target = new JfxDisplay(1280, 720),
      tracer = new PathTracer(new Scene(
        root = SceneNode(Array(boxRed, boxGreen, boxBlue)),
        skyMaterial = new UniformSky(Color.White * 2),
        camera = new PinholeCamera(
          position = Vec3d(0, 0, 9),
          forward = Vec3d.Back,
          up = Vec3d.Up))))
  }
}