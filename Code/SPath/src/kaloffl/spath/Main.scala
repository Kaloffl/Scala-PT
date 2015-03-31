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
import kaloffl.spath.scene.ReflectiveMaterial
import kaloffl.spath.scene.CheckeredMaterial
import kaloffl.spath.scene.shapes.Triangle
import kaloffl.spath.importer.PlyImporter
import kaloffl.spath.scene.DiffuseMaterial

/**
 * Entry 'class' to the program.
 */
object Main {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer
    val front = Vec3d(0, -2.5, -13).normalize
    val up = front.cross(Vec3d.RIGHT).normalize
    val camera = new Camera(Vec3d(0, 5, 13), front, up, 0.1f, 13f)

    val colorWhite = Vec3d(0.9, 0.9, 0.9)
    val colorBlack = Vec3d(0.1, 0.1, 0.1)

    val colorRed = Vec3d(0.9, 0.1, 0.1)
    val colorGreen = Vec3d(0.1, 0.9, 0.1)
    val colorBlue = Vec3d(0.1, 0.1, 0.9)

    val colorYellow = Vec3d(0.9, 0.9, 0.1)
    val colorCyan = Vec3d(0.1, 0.9, 0.9)
    val colorPink = Vec3d(0.9, 0.1, 0.9)

    val matWhiteDiffuse = new DiffuseMaterial(colorWhite)
    val matWhiteLight = new LightMaterial(Vec3d.WHITE * 2)
    val matWhiteDiffuseReflective = new AllroundMaterial(Vec3d.BLACK, colorWhite, 1.0f, 0.0f, 0.0f, 0.9f)

    val matWhiteGlass = new AllroundMaterial(Vec3d.BLACK, Vec3d.WHITE, 0.25f, 1.0f, 1.52f, 0.0f)
    val matWhiteMirror = new AllroundMaterial(Vec3d.BLACK, Vec3d.WHITE, 1.0f, 0.0f, 0.0f, 0.0f)
    val matWhiteGlassMirror = new AllroundMaterial(Vec3d.BLACK, Vec3d.WHITE, 1.0f, 1.0f, 1.52f, 0.0f)

    val matBlackDiffuse = new CheckeredMaterial(colorBlack, colorBlue)

    val matRedDiffuse = new DiffuseMaterial(colorRed)
    val matGreenDiffuse = new DiffuseMaterial(colorGreen)
    val matBlueDiffuse = new DiffuseMaterial(colorBlue)

    val matYellowDiffuse = new ReflectiveMaterial(colorYellow, 0.05)
    val matCyanDiffuse = new DiffuseMaterial(colorCyan)
    val matPinkDiffuse = new DiffuseMaterial(colorPink)

    val matBlackGlass = new AllroundMaterial(Vec3d.BLACK, Vec3d.BLACK, 0.25f, 1.0f, 1.52f, 0.0f)
    val matBlackMirror = new AllroundMaterial(Vec3d.BLACK, Vec3d.BLACK, 1.0f, 0.0f, 0.0f, 0.0f)
    val matBlackGlassMirror = new AllroundMaterial(Vec3d.BLACK, Vec3d.BLACK, 1.0f, 1.0f, 1.52f, 0.0f)

    val matBlueMirror = new AllroundMaterial(Vec3d.BLACK, Vec3d.BLUE, 1.0f, 0.0f, 0.0f, 0.9f)

    val matCyanGlass = new AllroundMaterial(Vec3d.BLACK, Vec3d(0.0f, 0.5f, 1.0f), 0.25f, 1.0f, 1.52f, 0.0f)

    val matCyanLight = new LightMaterial(Vec3d(4.0f, 8.0f, 16.0f))

    val matGrayDiffuse = new DiffuseMaterial(Vec3d(0.5))
    
    val objects = Array(

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

    val dragon = PlyImporter.load("D:/temp/dragon.ply", Vec3d(40), Vec3d(-0.5, -2, 0)).map { f ⇒ new SceneObject(f, matYellowDiffuse) }

    val allDiffuse = Array(

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
        new AABB(Vec3d(0, -0.5, 4), Vec3d(16, 1, 24)),
        matWhiteDiffuse),
      new SceneObject(
        new AABB(Vec3d(0, 8.5, 4), Vec3d(16, 1, 24)),
        matWhiteLight),
      new SceneObject(
        new AABB(Vec3d(8.5f, 4, 4), Vec3d(1, 8, 24)),
        matRedDiffuse),
      new SceneObject(
        new AABB(Vec3d(-8.5f, 4, 4), Vec3d(1, 8, 24)),
        matBlueDiffuse),
      new SceneObject(
        new AABB(Vec3d(0, 4, -8.5f), Vec3d(16, 8, 1)),
        matGreenDiffuse),
      new SceneObject(
        new AABB(Vec3d(0, 4, 16.5), Vec3d(16, 8, 1)),
        matBlackDiffuse))

    val mirrored = Array(
      new SceneObject(new Plane(Vec3d.UP, 0), matBlackDiffuse),
      new SceneObject(new Plane(Vec3d.DOWN, 8), matWhiteLight),
      new SceneObject(new Plane(Vec3d.LEFT, 8), matWhiteMirror),
      new SceneObject(new Plane(Vec3d.RIGHT, 8), matWhiteMirror),
      new SceneObject(new Plane(Vec3d.FRONT, 8), matWhiteMirror),
      new SceneObject(new Plane(Vec3d.BACK, 16), matWhiteMirror),

      new SceneObject(new Sphere(Vec3d(0, 2, 0), 2), matRedDiffuse))

    val scene = new Scene(allDiffuse ++ dragon, camera)
    pathTracer.render(display, scene, bounces = 32)
  }
}