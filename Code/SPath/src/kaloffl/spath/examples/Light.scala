package kaloffl.spath.examples

import kaloffl.spath.scene.Scene
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.DirectionalLightMaterial
import kaloffl.spath.math.Color
import kaloffl.spath.scene.Camera
import kaloffl.spath.PathTracer
import kaloffl.spath.Display
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.LightMaterial
import kaloffl.spath.scene.DiffuseMaterial
import kaloffl.spath.scene.RefractiveMaterial
import kaloffl.spath.scene.TransparentMaterial

object Light {

  def main(args: Array[String]): Unit = {

    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val colorRed = Color(0.9f, 0.1f, 0.1f)
    val colorGreen = Color(0.1f, 0.9f, 0.1f)
    val colorBlue = Color(0.1f, 0.1f, 0.9f)

    val matRedDiffuse = new DiffuseMaterial(colorRed)
    val matGreenDiffuse = new DiffuseMaterial(colorGreen)
    val matBlueDiffuse = new DiffuseMaterial(colorBlue)
    val matBlackDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteGlass8 = new RefractiveMaterial(Color.WHITE, 1.8, 0.0)

    val matAir = new TransparentMaterial(Color(0.8f, 0.9f, 0.95f), 0.1, 0.02, 1.0)

    val coloredLights = Array(
      new SceneObject(
        new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f),
        matWhiteGlass8), //matYellowDiffuse),
      new SceneObject(
        new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f),
        matWhiteDiffuse), //matCyanDiffuse),
      new SceneObject(
        new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f),
        matWhiteDiffuse), //matPinkDiffuse),
      new SceneObject(
        new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f),
        matWhiteDiffuse),
      new SceneObject(
        new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f),
        matWhiteDiffuse), //matBlackDiffuse),

      new SceneObject(
        AABB(Vec3d(-4, 7.5, 4), Vec3d(3, 0.125, 22)),
        new LightMaterial(colorRed, 2, 1024)),
      new SceneObject(
        AABB(Vec3d(0, 7.5, 4), Vec3d(3, 0.125, 22)),
        new LightMaterial(colorGreen, 2, 1024)),
      new SceneObject(
        AABB(Vec3d(4, 7.5, 4), Vec3d(3, 0.125, 22)),
        new LightMaterial(colorBlue, 2, 1024)),

      new SceneObject(
        AABB(Vec3d(-7.5, 7, 4), Vec3d(1, 2, 22)),
        matBlackDiffuse),
      new SceneObject(
        AABB(Vec3d(-2.5, 7, 4), Vec3d(2, 2, 22)),
        matBlackDiffuse),
      new SceneObject(
        AABB(Vec3d(2.5, 7, 4), Vec3d(2, 2, 22)),
        matBlackDiffuse),
      new SceneObject(
        AABB(Vec3d(7.5, 7, 4), Vec3d(1, 2, 22)),
        matBlackDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 7, -8.5), Vec3d(16, 2, 1)),
        matBlackDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 7, 16.5), Vec3d(16, 2, 1)),
        matBlackDiffuse),

      new SceneObject(
        AABB(Vec3d(0, -0.5, 4), Vec3d(16, 1, 24)),
        matWhiteDiffuse),
      //      new SceneObject(
      //        AABB(Vec3d(0, 8.5, 4), Vec3d(16, 1, 24)),
      //        matWhiteDiffuse),
      new SceneObject(
        AABB(Vec3d(8.5f, 4, 4), Vec3d(1, 8, 24)),
        matWhiteDiffuse), //matBlueDiffuse),
      new SceneObject(
        AABB(Vec3d(-8.5f, 4, 4), Vec3d(1, 8, 24)),
        matWhiteDiffuse), //matRedDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 4, -8.5f), Vec3d(16, 8, 1)),
        matWhiteDiffuse), //matGreenDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 4, 16.5), Vec3d(16, 8, 1)),
        matWhiteDiffuse))

    val front = Vec3d(0, -2.5, -13).normalize
    val up = front.cross(Vec3d.RIGHT).normalize
    val camera = new Camera(Vec3d(0, 5, 13), front, up, 0.1, Vec3d(0, -2.5, -13).length)

    val lightsScene = new Scene(coloredLights, camera, matAir, new DirectionalLightMaterial(Color.WHITE, 2, Vec3d(1, 3, 0).normalize, 1))

    pathTracer.render(display, lightsScene, bounces = 12)
  }
}