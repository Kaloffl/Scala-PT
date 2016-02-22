package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.filter.ScaleFilter
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.PinholeCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Plane
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.BoundlessNode
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.RecursivePathTracer

object Colorful {

  def main(args: Array[String]): Unit = {

    val matRedDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matYellowDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.1f))
    val matCyanDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.9f))
    val matPinkDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.9f))
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matAir = new TransparentMaterial(Color.Black)
    val matLight = new LightMaterial(Color.White * 2)

    val coloredSpheres = BoundlessNode(Array(
      SceneNode(new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f), matYellowDiffuse),
      SceneNode(new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f), matCyanDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f), matPinkDiffuse),
      SceneNode(new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f), matBlackDiffuse),

      SceneNode(AABB(Vec3d(0, 7.8, 4), Vec3d(12, 0.1, 20)), matLight),
      BoundlessNode(new Plane(Vec3d.Up, 0), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Down, 8), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Right, 8), matBlueDiffuse),
      BoundlessNode(new Plane(Vec3d.Left, 8), matRedDiffuse),
      BoundlessNode(new Plane(Vec3d.Front, 8), matGreenDiffuse),
      BoundlessNode(new Plane(Vec3d.Back, 16), matWhiteDiffuse)))

    RenderEngine.render(
      bounces = 6,
      target = new ScaleFilter(new JfxDisplay(1280, 720), 4, 4),
      tracer = new RecursivePathTracer(new Scene(
        root = coloredSpheres,
        airMedium = matAir,
        camera = new PinholeCamera(
          position = Vec3d(0, 2.5, 13),
          forward = Vec3d.Back,
          up = Vec3d.Up))))
  }
}