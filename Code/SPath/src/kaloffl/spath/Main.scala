package kaloffl.spath

import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.Material
import kaloffl.spath.scene.shapes.Plane
import kaloffl.spath.scene.shapes.AABB

/**
 * Entry 'class' to the program.
 */
object Main {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer
    val camera = new Camera(Vec3d(0, 2.5, 13), Vec3d.BACK, Vec3d.UP, 1, 10.5f)

    val colorWhite = Vec3d(0.9, 0.9, 0.9)
    val colorBlack = Vec3d(0.1, 0.1, 0.1)

    val colorRed = Vec3d(0.9, 0.1, 0.1)
    val colorGreen = Vec3d(0.1, 0.9, 0.1)
    val colorBlue = Vec3d(0.1, 0.1, 0.9)

    val colorYellow = Vec3d(0.9, 0.9, 0.1)
    val colorCyan = Vec3d(0.1, 0.9, 0.9)
    val colorPink = Vec3d(0.9, 0.1, 0.9)

    val matWhiteDiffuse = new Material(Vec3d.BLACK, colorWhite, 0.0f, 0.0f, 0.0f, 0.0f)
    val matWhiteLight = new Material(Vec3d.WHITE * 4, colorWhite, 0.0f, 0.0f, 0.0f, 0.0f)
    val matWhiteDiffuseReflective = new Material(Vec3d.BLACK, colorWhite, 0.1f, 0.0f, 0.0f, 0.0f)

    val matBlackDiffuse = new Material(Vec3d.BLACK, colorBlack, 0.0f, 0.0f, 0.0f, 0.0f)

    val matRedDiffuse = new Material(Vec3d.BLACK, colorRed, 0.0f, 0.0f, 0.0f, 0.0f)
    val matGreenDiffuse = new Material(Vec3d.BLACK, colorGreen, 0.0f, 0.0f, 0.0f, 0.0f)
    val matBlueDiffuse = new Material(Vec3d.BLACK, colorBlue, 0.0f, 0.0f, 0.0f, 0.0f)

    val matYellowDiffuse = new Material(Vec3d.BLACK, colorYellow, 0.0f, 0.0f, 0.0f, 0.0f)
    val matCyanDiffuse = new Material(Vec3d.BLACK, colorCyan, 0.0f, 0.0f, 0.0f, 0.0f)
    val matPinkDiffuse = new Material(Vec3d.BLACK, colorPink, 0.0f, 0.0f, 0.0f, 0.0f)

    val matBlackGlass = new Material(Vec3d.BLACK, Vec3d.BLACK, 0.25f, 1.0f, 1.52f, 0.0f)
    val matBlackMirror = new Material(Vec3d.BLACK, Vec3d.BLACK, 1.0f, 0.0f, 0.0f, 0.0f)

    val matBlueMirror = new Material(Vec3d.BLACK, Vec3d.BLUE, 1.0f, 0.0f, 0.0f, 0.9f)

    val matCyanGlass = new Material(Vec3d.BLACK, Vec3d(0.0f, 0.5f, 1.0f), 0.25f, 1.0f, 1.52f, 0.0f)

    val matCyanLight = new Material(Vec3d(4.0f, 8.0f, 16.0f), Vec3d.BLACK, 0.0f, 0.0f, 0.0f, 0.0f)

    val objects = Seq(

      //      new SceneObject(
      //        new Sphere(Vec3d(0.0f, 8.0f, 0.0f), 1.25f),
      //        matCyanLight),

      new SceneObject(
        new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f),
        matBlackGlass),
      new SceneObject(
        new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f),
        matBlueMirror),
      new SceneObject(
        new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f),
        matBlackMirror),
      new SceneObject(
        new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f),
        matCyanGlass),
      new SceneObject(
        new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f),
        matWhiteDiffuseReflective),

      //      new SceneObject(
      //        new AABB(Vec3d(0, 8, 0), Vec3d(2, 0.1, 2)),
      //        matWhiteLight),

      new SceneObject(
        new Plane(Vec3d.UP, 0.0f),
        matWhiteDiffuse),
      new SceneObject(
        new Plane(Vec3d.DOWN, 8.0f),
        matWhiteLight),
      new SceneObject(
        new Plane(Vec3d.LEFT, 8.0f),
        matRedDiffuse),
      new SceneObject(
        new Plane(Vec3d.RIGHT, 8.0f),
        matBlueDiffuse),
      new SceneObject(
        new Plane(Vec3d.FRONT, 8.0f),
        matGreenDiffuse),
      new SceneObject(
        new Plane(Vec3d.BACK, 16.0f),
        matBlackDiffuse))

    val allDiffuse = Seq(

      new SceneObject(
        new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f),
        matYellowDiffuse),
      new SceneObject(
        new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f),
        matCyanDiffuse),
      new SceneObject(
        new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f),
        matPinkDiffuse),
      new SceneObject(
        new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f),
        matWhiteDiffuse),
      new SceneObject(
        new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f),
        matBlackDiffuse),

      new SceneObject(
        new Plane(Vec3d.UP, 0.0f),
        matWhiteDiffuse),
      new SceneObject(
        new Plane(Vec3d.DOWN, 8.0f),
        matWhiteLight),
      new SceneObject(
        new Plane(Vec3d.LEFT, 8.0f),
        matRedDiffuse),
      new SceneObject(
        new Plane(Vec3d.RIGHT, 8.0f),
        matBlueDiffuse),
      new SceneObject(
        new Plane(Vec3d.FRONT, 8.0f),
        matGreenDiffuse),
      new SceneObject(
        new Plane(Vec3d.BACK, 16.0f),
        matBlackDiffuse))

    val scene = new Scene(allDiffuse, camera)
    pathTracer.render(display, 1000, scene)
  }
}