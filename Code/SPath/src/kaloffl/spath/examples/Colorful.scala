package kaloffl.spath.examples

import kaloffl.spath.scene.Scene
import kaloffl.spath.PathTracer
import kaloffl.spath.Display
import kaloffl.spath.scene.Camera
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.math.Color
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.structure.FlatObject
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.math.Attenuation

object Colorful {

  def main(args: Array[String]): Unit = {
    val display = new Display(128, 72)
    val pathTracer = new PathTracer

    val matRedDiffuse = new DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = new DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matYellowDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.1f))
    val matCyanDiffuse = new DiffuseMaterial(Color(0.1f, 0.9f, 0.9f))
    val matPinkDiffuse = new DiffuseMaterial(Color(0.9f, 0.1f, 0.9f))
    val matBlackDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matAir = new TransparentMaterial(Color(0.2f, 0.1f, 0.05f), 0.0, 0.0, 1.0)

            val light = new FlatObject(
            AABB(Vec3d(0, 7.8, 4), Vec3d(12, 0.1, 20)),
            new LightMaterial(Color.WHITE, 2f, Attenuation.none))
//    val light = new FlatObject(
//      Array[Shape](
//          new Sphere(Vec3d(-1.1, 6.9, 1.9), 1),
//          new Sphere(Vec3d(-1.1, 6.9, 4.1), 1),
//          new Sphere(Vec3d(1.1, 6.9, 1.9), 1),
//          new Sphere(Vec3d(1.1, 6.9, 4.1), 1)),
//      new LightMaterial(Color.WHITE, 8f, Attenuation.radius(1)))

    val coloredSpheres = SceneNode(Array(
      SceneNode(
        new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f),
        matYellowDiffuse),
      SceneNode(
        new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f),
        matCyanDiffuse),
      SceneNode(
        new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f),
        matPinkDiffuse),
      SceneNode(
        new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f),
        matWhiteDiffuse),
      SceneNode(
        new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f),
        matBlackDiffuse),

      SceneNode(
        AABB(Vec3d(0, -0.5, 4), Vec3d(16, 1, 24)),
        matWhiteDiffuse),
      new FlatObject(
        AABB(Vec3d(0, 8.5, 4), Vec3d(16, 1, 24)),
        matWhiteDiffuse),
      light,
      //      SceneNode(
      //        AABB(Vec3d(0, 7, 4), Vec3d(14, 0.125, 22)),
      //        matWhiteDiffuse),
      SceneNode(
        AABB(Vec3d(8.5f, 4, 4), Vec3d(1, 8, 24)),
        matBlueDiffuse),
      SceneNode(
        AABB(Vec3d(-8.5f, 4, 4), Vec3d(1, 8, 24)),
        matRedDiffuse),
      SceneNode(
        AABB(Vec3d(0, 4, -8.5f), Vec3d(16, 8, 1)),
        matGreenDiffuse),
      SceneNode(
        AABB(Vec3d(0, 4, 16.5), Vec3d(16, 8, 1)),
        matWhiteDiffuse)))

    val lowCamera = new Camera(Vec3d(0, 2.5, 13), Vec3d.BACK, Vec3d.UP, 0.0f, 13);

    val colorfulScene = new Scene(coloredSpheres, lowCamera, matAir, matBlackDiffuse, lights = Array(light))

    pathTracer.render(display, colorfulScene, bounces = 6)
  }
}