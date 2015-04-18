package kaloffl.spath

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.AllroundMaterial
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.CheckeredMask
import kaloffl.spath.scene.DiffuseMaterial
import kaloffl.spath.scene.DiffuseMaterial
import kaloffl.spath.scene.LightMaterial
import kaloffl.spath.scene.Mask
import kaloffl.spath.scene.MaskedMaterial
import kaloffl.spath.scene.ReflectiveMaterial
import kaloffl.spath.scene.RefractiveMaterial
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.importer.PlyImporter
import kaloffl.spath.scene.shapes.Triangle

/**
 * Entry 'class' to the program.
 */
object Main {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer
    val front = Vec3d(0, -2.5, -13).normalize
    val up = front.cross(Vec3d.RIGHT).normalize
    val camera = new Camera(Vec3d(0, 5, 13), front, up, 0.1f, Vec3d(0, -2.5, -13).length)

    val lowCamera = new Camera(Vec3d(0, 2.5, 13), Vec3d.BACK, Vec3d.UP, 0.0f, 13);

    val colorWhite = Color(0.9f, 0.9f, 0.9f)
    val colorBlack = Color(0.1f, 0.1f, 0.1f)

    val colorRed = Color(0.9f, 0.1f, 0.1f)
    val colorGreen = Color(0.1f, 0.9f, 0.1f)
    val colorBlue = Color(0.1f, 0.1f, 0.9f)

    val colorYellow = Color(0.9f, 0.9f, 0.1f)
    val colorCyan = Color(0.1f, 0.9f, 0.9f)
    val colorPink = Color(0.9f, 0.1f, 0.9f)

    val matWhiteDiffuse = new DiffuseMaterial(colorWhite)
    val matWhiteLight = new LightMaterial(Color.WHITE, 1 /*.42f*/ , 1024)
    val matWhiteDiffuseReflective = new AllroundMaterial(colorWhite, 0.0f, 1.0f, 0.0f, 0.0f, 0.9f)

    val matRedDiffuse = new DiffuseMaterial(colorRed)
    val matGreenDiffuse = new DiffuseMaterial(colorGreen)
    val matBlueDiffuse = new DiffuseMaterial(colorBlue)

    val matBlackDiffuse = new DiffuseMaterial(colorBlack)

    val matWhiteGlass0 = new RefractiveMaterial(Color.WHITE, 1.0, 0.0)
    val matWhiteGlass1 = new RefractiveMaterial(Color.WHITE, 1.1, 0.0)
    val matWhiteGlass2 = new RefractiveMaterial(Color.WHITE, 1.2, 0.0)
    val matWhiteGlass3 = new RefractiveMaterial(Color.WHITE, 1.3, 0.0)
    val matWhiteGlass4 = new RefractiveMaterial(Color.WHITE, 1.4, 0.0)
    val matWhiteGlass5 = new RefractiveMaterial(Color.WHITE, 1.5, 0.0)
    val matWhiteGlass6 = new RefractiveMaterial(Color.WHITE, 1.6, 0.0)
    val matWhiteGlass7 = new RefractiveMaterial(Color.WHITE, 1.7, 0.0)
    val matWhiteGlass8 = new RefractiveMaterial(Color.WHITE, 1.8, 0.0)
    val matWhiteGlass9 = new RefractiveMaterial(Color.WHITE, 1.9, 0.0)

    val matWhiteMirror = new ReflectiveMaterial(Color.WHITE, 0.0f)
    val matWhiteGlassMirror = new AllroundMaterial(Color.WHITE, 0.0f, 1.0f, 1.0f, 1.52f, 0.0f)

    val matYellowDiffuse = new DiffuseMaterial(colorYellow)
    val matCyanDiffuse = new DiffuseMaterial(colorCyan)
    val matPinkDiffuse = new DiffuseMaterial(colorPink)

    val matBlackGlass = new AllroundMaterial(Color.BLACK, 0.0f, 0.25f, 1.0f, 1.52f, 0.0f)
    val matBlackMirror = new AllroundMaterial(Color.BLACK, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f)
    val matBlackGlassMirror = new AllroundMaterial(Color.BLACK, 0.0f, 1.0f, 1.0f, 1.52f, 0.0f)

    val checkeredMask = new CheckeredMask(2)
    val matBlackBlueCheckered = new MaskedMaterial(matBlackDiffuse, matBlueDiffuse, checkeredMask)
    val halfMask = new Mask() {
      override def maskAmount(pos: Vec3d): Float = 0.5f
    }
    val gradientMask = new Mask() {
      override def maskAmount(pos: Vec3d): Float = (pos.x.toFloat + 8) / 16
    }
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)
    val matRedGreenGradient = new MaskedMaterial(matRedDiffuse, matGreenDiffuse, gradientMask)

    val matBlueMirror = new AllroundMaterial(Color.BLUE, 0.0f, 1.0f, 0.0f, 0.0f, 0.9f)
    val matYellowMirror = new ReflectiveMaterial(colorYellow, 0.05)

    val matCyanGlass = new AllroundMaterial(Color(0.0f, 0.5f, 1.0f), 0.0f, 0.25f, 1.0f, 1.52f, 0.0f)

    val matCyanLight = new LightMaterial(Color(0.25f, 0.5f, 1.0f), 16f, 1024)

    val matRedGlass = new RefractiveMaterial(colorRed, 1.52, 0.0)

    val matGrayDiffuse = new DiffuseMaterial(Color(0.5f, 0.5f, 0.5f))

    val matRedLight = new LightMaterial(Color.RED, 1.0f, 1024)
    val matGreenLight = new LightMaterial(Color.GREEN, 1.0f, 1024)

    val coloredSpheres = Array(

      new SceneObject(
        new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f),
        matWhiteDiffuse),
      new SceneObject(
        new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f),
        matWhiteDiffuse),
      new SceneObject(
        new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f),
        matRedDiffuse),
      new SceneObject(
        new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f),
        matWhiteDiffuse),
      new SceneObject(
        new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f),
        matWhiteDiffuse),

      new SceneObject(
        AABB(Vec3d(0, -0.5, 4), Vec3d(16, 1, 24)),
        matWhiteDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 8.5, 4), Vec3d(16, 1, 24)),
        new LightMaterial(Color.WHITE, 1.2f, 1024f)),
      new SceneObject(
        AABB(Vec3d(8.5f, 4, 4), Vec3d(1, 8, 24)),
        matWhiteDiffuse),
      new SceneObject(
        AABB(Vec3d(-8.5f, 4, 4), Vec3d(1, 8, 24)),
        matWhiteDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 4, -8.5f), Vec3d(16, 8, 1)),
        matWhiteDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 4, 16.5), Vec3d(16, 8, 1)),
        matWhiteDiffuse))

    val objects = Array(

      new SceneObject(
        new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f),
        matWhiteGlass1),
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

      //      new SceneObject(
      //        PlyImporter.load("D:/temp/dragon.ply", Vec3d(40), Vec3d(-0.5, -2, 0)),
      //        matYellowMirror),
      //
      //      new SceneObject(
      //        PlyImporter.load("D:/temp/bunny.ply", Vec3d(20), Vec3d(0, -0.5, 4)),
      //        matWhiteGlass),
      //
      //      new SceneObject(
      //        PlyImporter.load("D:/temp/lucy.ply", Vec3d(40), Vec3d(-0.5, -2, 0)),
      //        matWhiteGlass9),

      new SceneObject(
        AABB(Vec3d(0, -0.5, 4), Vec3d(16, 1, 24)),
        matGrayDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 8.5, 4), Vec3d(16, 1, 24)),
        matWhiteLight),
      new SceneObject(
        AABB(Vec3d(8.5f, 4, 4), Vec3d(1, 8, 24)),
        matRedDiffuse),
      new SceneObject(
        AABB(Vec3d(-8.5f, 4, 4), Vec3d(1, 8, 24)),
        matBlueDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 4, -8.5f), Vec3d(16, 8, 1)),
        matGreenDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 4, 16.5), Vec3d(16, 8, 1)),
        matWhiteDiffuse))

    val glassTest = Array(
      new SceneObject(
        new Sphere(Vec3d(-9, 1, 0), 1),
        matWhiteGlass0),
      new SceneObject(
        new Sphere(Vec3d(-7, 1, 0), 1),
        matWhiteGlass1),
      new SceneObject(
        new Sphere(Vec3d(-5, 1, 0), 1),
        matWhiteGlass2),
      new SceneObject(
        new Sphere(Vec3d(-3, 1, 0), 1),
        matWhiteGlass3),
      new SceneObject(
        new Sphere(Vec3d(-1, 1, 0), 1),
        matWhiteGlass4),
      new SceneObject(
        new Sphere(Vec3d(1, 1, 0), 1),
        matWhiteGlass5),
      new SceneObject(
        new Sphere(Vec3d(3, 1, 0), 1),
        matWhiteGlass6),
      new SceneObject(
        new Sphere(Vec3d(5, 1, 0), 1),
        matWhiteGlass7),
      new SceneObject(
        new Sphere(Vec3d(7, 1, 0), 1),
        matWhiteGlass8),
      new SceneObject(
        new Sphere(Vec3d(9, 1, 0), 1),
        matWhiteGlass9),

      new SceneObject(
        AABB(Vec3d(0, 8.5, 4), Vec3d(20, 1, 24)),
        matWhiteLight),

      new SceneObject(
        AABB(Vec3d(0, -0.5, 4), Vec3d(20, 1, 24)),
        matBlackWhiteCheckered),
      new SceneObject(
        AABB(Vec3d(10.5f, 4, 4), Vec3d(1, 8, 24)),
        matRedDiffuse),
      new SceneObject(
        AABB(Vec3d(-10.5f, 4, 4), Vec3d(1, 8, 24)),
        matBlueDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 4, -8.5f), Vec3d(20, 8, 1)),
        matGreenDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 4, 16.5), Vec3d(20, 8, 1)),
        matWhiteDiffuse))

    val lightTest = Array(
      new SceneObject(
        new Sphere(Vec3d(0, 2, 0), 1),
        //        AABB(Vec3d(0, 2, 0), Vec3d(2)),
        matWhiteLight),
      new SceneObject(
        new Sphere(Vec3d(0, 5, 0), 1),
        matWhiteDiffuse),
      new SceneObject(
        new Sphere(Vec3d(3, 5, 0), 1),
        matWhiteDiffuse),
      new SceneObject(
        new Sphere(Vec3d(3, 2, 0), 1),
        matWhiteDiffuse),
      new SceneObject(
        new AABB(Vec3d(-10, -1, -10), Vec3d(10, 0, 10)),
        matWhiteDiffuse))

    val cmatWhite = new DiffuseMaterial(Color(0.76f, 0.75f, 0.5f))
    val cmatRed = new DiffuseMaterial(Color(0.63f, 0.06f, 0.04f))
    val cmatGreen = new DiffuseMaterial(Color(0.15f, 0.48f, 0.09f))
    val cmatLight = new LightMaterial(Color.WHITE, 1, 512)

    val cornellBox = Array(
      new SceneObject(
        new Sphere(Vec3d(0, 8, 0), 1),
        cmatLight),

      //      new SceneObject(
      //        PlyImporter.load("D:/temp/bunny_flipped.ply", Vec3d(30), Vec3d(-4, -0.989622, 0)),
      //        cmatWhite),

      new SceneObject(
        AABB(Vec3d(0, 8.5, 4), Vec3d(16, 1, 24)),
        cmatWhite),
      new SceneObject(
        AABB(Vec3d(0, -0.5, 4), Vec3d(16, 1, 24)),
        cmatWhite),
      new SceneObject(
        AABB(Vec3d(8.5f, 4, 4), Vec3d(1, 8, 24)),
        cmatRed),
      new SceneObject(
        AABB(Vec3d(-8.5f, 4, 4), Vec3d(1, 8, 24)),
        cmatGreen),
      new SceneObject(
        AABB(Vec3d(0, 4, -8.5f), Vec3d(16, 8, 1)),
        cmatWhite),
      new SceneObject(
        AABB(Vec3d(0, 4, 16.5), Vec3d(16, 8, 1)),
        cmatWhite))
    val cornellCam = new Camera(Vec3d(-3, 5, 13), front, up, 0.01f, 13.0f)
    //    val cornellScene = new Scene(cornellBox, cornellCam)
    //    val dragonScene = new Scene(objects, camera)
    val colorfulScene = new Scene(glassTest, lowCamera)

    //    BvhToFile.toFile(scene.bvh, "bvhCollapsed.txt")
    pathTracer.render(display, colorfulScene, bounces = 8)
  }
}