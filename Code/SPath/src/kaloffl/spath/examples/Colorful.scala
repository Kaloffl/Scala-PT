package kaloffl.spath.examples

import kaloffl.spath.Display
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer

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
    val matLight = new LightMaterial(Color.White * 2, Attenuation.none)

    val coloredSpheres = SceneNode(Array(
      SceneNode(new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f), matYellowDiffuse),
      SceneNode(new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f), matCyanDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f), matPinkDiffuse),
      SceneNode(new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f), matBlackDiffuse),

      SceneNode(AABB(Vec3d(0, -0.5, 4), Vec3d(16, 1, 24)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(0, 8.5, 4), Vec3d(16, 1, 24)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(0, 7.8, 4), Vec3d(12, 0.1, 20)), matLight),
      SceneNode(AABB(Vec3d(8.5f, 4, 4), Vec3d(1, 8, 24)), matBlueDiffuse),
      SceneNode(AABB(Vec3d(-8.5f, 4, 4), Vec3d(1, 8, 24)), matRedDiffuse),
      SceneNode(AABB(Vec3d(0, 4, -8.5f), Vec3d(16, 8, 1)), matGreenDiffuse),
      SceneNode(AABB(Vec3d(0, 4, 16.5), Vec3d(16, 8, 1)), matWhiteDiffuse)))

    RenderEngine.render(
      bounces = 6,
      target = new Display(1280, 720),
      tracer = new PathTracer(new Scene(
        root = coloredSpheres,
        airMedium = matAir,
        camera = new Camera(
          position = Vec3d(0, 2.5, 13),
          forward = Vec3d.Back,
          up = Vec3d.Up))))
  }
}