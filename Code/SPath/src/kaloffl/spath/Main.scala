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
import kaloffl.spath.scene.DiffuseMaterial
import kaloffl.spath.scene.LightMaterial
import kaloffl.spath.scene.AllroundMaterial

/**
 * Entry 'class' to the program.
 */
object Main {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer
    val camera = new Camera(Vec3d(0, 2.5, 13), Vec3d.BACK, Vec3d.UP, 0, 13f)

    val colorWhite = Vec3d(0.9, 0.9, 0.9)
    val colorBlack = Vec3d(0.1, 0.1, 0.1)

    val colorRed = Vec3d(0.9, 0.1, 0.1)
    val colorGreen = Vec3d(0.1, 0.9, 0.1)
    val colorBlue = Vec3d(0.1, 0.1, 0.9)

    val colorYellow = Vec3d(0.9, 0.9, 0.1)
    val colorCyan = Vec3d(0.1, 0.9, 0.9)
    val colorPink = Vec3d(0.9, 0.1, 0.9)

    val matWhiteDiffuse = new DiffuseMaterial(colorWhite)
    val matWhiteLight = new LightMaterial(Vec3d.WHITE)
    val matWhiteDiffuseReflective = new AllroundMaterial(Vec3d.BLACK, colorWhite, 1.0f, 0.0f, 0.0f, 0.9f)

    val matWhiteGlass = new AllroundMaterial(Vec3d.BLACK, Vec3d.WHITE, 0.25f, 1.0f, 1.52f, 0.0f)
    val matWhiteMirror = new AllroundMaterial(Vec3d.BLACK, Vec3d.WHITE, 1.0f, 0.0f, 0.0f, 0.0f)
    val matWhiteGlassMirror = new AllroundMaterial(Vec3d.BLACK, Vec3d.WHITE, 1.0f, 1.0f, 1.52f, 0.0f)

    val matBlackDiffuse = new DiffuseMaterial(colorBlack)

    val matRedDiffuse = new DiffuseMaterial(colorRed)
    val matGreenDiffuse = new DiffuseMaterial(colorGreen)
    val matBlueDiffuse = new DiffuseMaterial(colorBlue)

    val matYellowDiffuse = new DiffuseMaterial(colorYellow)
    val matCyanDiffuse = new DiffuseMaterial(colorCyan)
    val matPinkDiffuse = new DiffuseMaterial(colorPink)

    val matBlackGlass = new AllroundMaterial(Vec3d.BLACK, Vec3d.BLACK, 0.25f, 1.0f, 1.52f, 0.0f)
    val matBlackMirror = new AllroundMaterial(Vec3d.BLACK, Vec3d.BLACK, 1.0f, 0.0f, 0.0f, 0.0f)
    val matBlackGlassMirror = new AllroundMaterial(Vec3d.BLACK, Vec3d.BLACK, 1.0f, 1.0f, 1.52f, 0.0f)

    val matBlueMirror = new AllroundMaterial(Vec3d.BLACK, Vec3d.BLUE, 1.0f, 0.0f, 0.0f, 0.9f)

    val matCyanGlass = new AllroundMaterial(Vec3d.BLACK, Vec3d(0.0f, 0.5f, 1.0f), 0.25f, 1.0f, 1.52f, 0.0f)

    val matCyanLight = new LightMaterial(Vec3d(4.0f, 8.0f, 16.0f))

    val objects = Seq(

      //      new SceneObject(
      //        new Sphere(Vec3d(0.0f, 8.0f, 0.0f), 1.25f),
      //        matCyanLight),

      new SceneObject(
        new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f),
        matWhiteGlass),
      new SceneObject(
        new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f),
        matBlueMirror),
      new SceneObject(
        new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f),
        matWhiteMirror),
      new SceneObject(
        new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f),
        matCyanGlass),
      new SceneObject(
        new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f),
        matWhiteDiffuseReflective),

      new SceneObject(
        new AABB(Vec3d(0, 9, 4), Vec3d(16, 2, 24)),
        matWhiteLight),

      new SceneObject(
        new Plane(Vec3d.UP, 0.0f),
        matWhiteDiffuse),
      //      new SceneObject(
      //        new Plane(Vec3d.DOWN, 8.0f),
      //        matWhiteLight),
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
      // new SceneObject(
      //   new Plane(Vec3d.DOWN, 8.0f),
      //   matWhiteLight),
      new SceneObject(
        new AABB(Vec3d(0, 9, 4), Vec3d(16, 2, 24)),
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
        
    val mirrored = Seq(
        new SceneObject(new Plane(Vec3d.UP, 0), matBlackDiffuse),
        new SceneObject(new Plane(Vec3d.DOWN, 8), matWhiteLight),
        new SceneObject(new Plane(Vec3d.LEFT, 8), matWhiteMirror),
        new SceneObject(new Plane(Vec3d.RIGHT, 8), matWhiteMirror),
        new SceneObject(new Plane(Vec3d.FRONT, 8), matWhiteMirror),
        new SceneObject(new Plane(Vec3d.BACK, 16), matWhiteMirror),
        
        new SceneObject(new Sphere(Vec3d(0, 2, 0), 2), matRedDiffuse)
    )

    val scene = new Scene(allDiffuse, camera)
    pathTracer.render(display, scene, bounces = 32)
  }
}