package kaloffl.spath.examples

import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.scene.materials.{DiffuseMaterial, TransparentMaterial, UniformSky}
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.{PinholeCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.PathTracer
import kaloffl.spath.{JfxDisplay, RenderEngine}

object Nested {

  def main(args: Array[String]): Unit = {

    val matGlassRed = new TransparentMaterial(
      volumeColor = Color(0.1f, 0.9f, 0.9f),
      absorbtionDepth = 0.75f)
    val matGlassGreen = new TransparentMaterial(
      volumeColor = Color(0.9f, 0.1f, 0.9f),
      absorbtionDepth = 0.5f)
    val matGlassBlue = new TransparentMaterial(
      volumeColor = Color(0.9f, 0.9f, 0.1f),
      absorbtionDepth = 0.25f)
    val matFloor = DiffuseMaterial(Color(0.6f, 0.6f, 0.6f))

    val boxRed = SceneNode(AABB(Vec3d(0, 0, 0), Vec3d(2, 2, 3)), matGlassRed)
    val boxGreen = SceneNode(AABB(Vec3d(0, 0, 0), Vec3d(3, 1, 2)), matGlassGreen)
    val boxBlue = SceneNode(AABB(Vec3d(0, 0, 0), Vec3d(1, 3, 1)), matGlassBlue)

    RenderEngine.render(
      target = new JfxDisplay(1280, 720),
      tracer = new PathTracer(maxBounces = 12),
      view = new Viewpoint(
        position = Vec3d(0, 0, 9),
        forward = Vec3d.Back,
        up = Vec3d.Up),
      scene = new Scene(
        root = SceneNode(Array(boxRed, boxGreen, boxBlue)),
        skyMaterial = new UniformSky(Color.White * 2),
        camera = new PinholeCamera))
  }
}