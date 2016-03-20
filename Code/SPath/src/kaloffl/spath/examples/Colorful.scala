package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.filter.ScaleFilter
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.PinholeCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.Viewpoint
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Plane
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.BoundlessNode
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.RecursivePathTracer
import kaloffl.spath.RtApplication
import kaloffl.spath.scene.materials.RefractiveMaterial
import kaloffl.spath.tracing.PathTracer
import kaloffl.spath.filter.BloomFilter
import kaloffl.spath.scene.shapes.Triangle
import kaloffl.spath.scene.hints.GlobalHint
import kaloffl.spath.tracing.RayTracer

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
    val matGlass = RefractiveMaterial(Color.White, 2)

//    val matLight = LightMaterial(Color.White * 2)
//    val light1 = new Triangle(Vec3d(-6, 7.8, -6), Vec3d(-6, 7.8, 14), Vec3d(6, 7.8, 14))
//    val light2 = new Triangle(Vec3d(-6, 7.8, -6), Vec3d(6, 7.8, -6), Vec3d(6, 7.8, 14))

    val matLight = LightMaterial(Color.White * 10)
    val light1 = new Sphere(Vec3d(-4, 7.5, 4), 0.5f)
    val light2 = new Sphere(Vec3d(4, 7.5, -4), 0.5f)

    val coloredSpheres = BoundlessNode(Array(
      SceneNode(new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f), matYellowDiffuse),
      SceneNode(new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f), matCyanDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f), matPinkDiffuse),
      SceneNode(new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f), matGlass),

//      SceneNode(AABB(Vec3d(0, 7.8, 4), Vec3d(12, 0.1, 20)), matLight),
      SceneNode(light1, matLight),
      SceneNode(light2, matLight),
      BoundlessNode(new Plane(Vec3d.Up, 0), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Down, 8), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Right, 8), matBlueDiffuse),
      BoundlessNode(new Plane(Vec3d.Left, 8), matRedDiffuse),
      BoundlessNode(new Plane(Vec3d.Front, 8), matGreenDiffuse),
      BoundlessNode(new Plane(Vec3d.Back, 16), matWhiteDiffuse)))

    val window = new JfxDisplay(1280, 720)

    RtApplication.run(
      bounces = 4,
      events = window.events,
      target = window,
      tracer = RayTracer,
      initialView = new Viewpoint(
        position = Vec3d(0, 2.5, 13),
        forward = Vec3d.Back,
        up = Vec3d.Up),
      scene = new Scene(
        root = coloredSpheres,
        lightHints = Array(new GlobalHint(light1), new GlobalHint(light2)),
        camera = new PinholeCamera))
  }
}