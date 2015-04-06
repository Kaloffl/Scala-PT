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
import kaloffl.spath.scene.shapes.Triangle
import kaloffl.spath.importer.PlyImporter
import kaloffl.spath.scene.DiffuseMaterial
import kaloffl.spath.math.Color
import kaloffl.spath.scene.RefractiveMaterial
import kaloffl.spath.bvh.BvhToFile
import kaloffl.spath.scene.MaskedMaterial
import kaloffl.spath.scene.CheckeredMask
import kaloffl.spath.scene.Mask

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

    val colorWhite = Color(0.9f, 0.9f, 0.9f)
    val colorBlack = Color(0.1f, 0.1f, 0.1f)

    val colorRed = Color(0.9f, 0.1f, 0.1f)
    val colorGreen = Color(0.1f, 0.9f, 0.1f)
    val colorBlue = Color(0.1f, 0.1f, 0.9f)

    val colorYellow = Color(0.9f, 0.9f, 0.1f)
    val colorCyan = Color(0.1f, 0.9f, 0.9f)
    val colorPink = Color(0.9f, 0.1f, 0.9f)

    val matWhiteDiffuse = new DiffuseMaterial(colorWhite)
    val matWhiteLight = new LightMaterial(Color.WHITE * 1.42f)
    val matWhiteDiffuseReflective = new AllroundMaterial(Color.BLACK, colorWhite, 1.0f, 0.0f, 0.0f, 0.9f)

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
    val matWhiteGlassMirror = new AllroundMaterial(Color.BLACK, Color.WHITE, 1.0f, 1.0f, 1.52f, 0.0f)

    val matYellowDiffuse = new DiffuseMaterial(colorYellow)
    val matCyanDiffuse = new DiffuseMaterial(colorCyan)
    val matPinkDiffuse = new DiffuseMaterial(colorPink)

    val matBlackGlass = new AllroundMaterial(Color.BLACK, Color.BLACK, 0.25f, 1.0f, 1.52f, 0.0f)
    val matBlackMirror = new AllroundMaterial(Color.BLACK, Color.BLACK, 1.0f, 0.0f, 0.0f, 0.0f)
    val matBlackGlassMirror = new AllroundMaterial(Color.BLACK, Color.BLACK, 1.0f, 1.0f, 1.52f, 0.0f)

    val checkeredMask = new CheckeredMask(2)
    val matBlackBlueCheckered = new MaskedMaterial(matBlackDiffuse, matBlueDiffuse, checkeredMask)
    val halfMask = new Mask() {
      override def maskAmount(pos: Vec3d): Float = 0.5f
    }
    val gradientMask = new Mask() {
      override def maskAmount(pos: Vec3d): Float = (pos.x.toFloat + 8) / 16
    }
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)
    val matRedGreenGradient  = new MaskedMaterial(matRedDiffuse, matGreenDiffuse, gradientMask)

    val matBlueMirror = new AllroundMaterial(Color.BLACK, Color.BLUE, 1.0f, 0.0f, 0.0f, 0.9f)
    val matYellowMirror = new ReflectiveMaterial(colorYellow, 0.05)

    val matCyanGlass = new AllroundMaterial(Color.BLACK, Color(0.0f, 0.5f, 1.0f), 0.25f, 1.0f, 1.52f, 0.0f)

    val matCyanLight = new LightMaterial(Color(4.0f, 8.0f, 16.0f))

    val matRedGlass = new RefractiveMaterial(colorRed, 1.52, 0.0)

    val matGrayDiffuse = new DiffuseMaterial(Color(0.5f, 0.5f, 0.5f))

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
        matWhiteGlass0
      ),
      new SceneObject(
        new Sphere(Vec3d(-7, 1, 0), 1),
        matWhiteGlass1
      ),
      new SceneObject(
        new Sphere(Vec3d(-5, 1, 0), 1),
        matWhiteGlass2
      ),
      new SceneObject(
        new Sphere(Vec3d(-3, 1, 0), 1),
        matWhiteGlass3
      ),
      new SceneObject(
        new Sphere(Vec3d(-1, 1, 0), 1),
        matWhiteGlass4
      ),
      new SceneObject(
        new Sphere(Vec3d(1, 1, 0), 1),
        matWhiteGlass5
      ),
      new SceneObject(
        new Sphere(Vec3d(3, 1, 0), 1),
        matWhiteGlass6
      ),
      new SceneObject(
        new Sphere(Vec3d(5, 1, 0), 1),
        matWhiteGlass7
      ),
      new SceneObject(
        new Sphere(Vec3d(7, 1, 0), 1),
        matWhiteGlass8
      ),
      new SceneObject(
        new Sphere(Vec3d(9, 1, 0), 1),
        matWhiteGlass9
      ),
        
      new SceneObject(
        AABB(Vec3d(0, -0.5, 4), Vec3d(20, 1, 24)),
        matBlackWhiteCheckered),
      new SceneObject(
        AABB(Vec3d(0, 8.5, 4), Vec3d(20, 1, 24)),
        matWhiteLight),
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

    val mirrored = Array(
      new SceneObject(new Plane(Vec3d.UP, 0), matBlackDiffuse),
      new SceneObject(new Plane(Vec3d.DOWN, 8), matWhiteLight),
      new SceneObject(new Plane(Vec3d.LEFT, 8), matWhiteMirror),
      new SceneObject(new Plane(Vec3d.RIGHT, 8), matWhiteMirror),
      new SceneObject(new Plane(Vec3d.FRONT, 8), matWhiteMirror),
      new SceneObject(new Plane(Vec3d.BACK, 16), matWhiteMirror),

      new SceneObject(new Sphere(Vec3d(0, 2, 0), 2), matRedDiffuse))

    val scene = new Scene(glassTest, camera)
    //    BvhToFile.toFile(scene.bvh, "bvhCollapsed.txt")
    pathTracer.render(display, scene, bounces = 16)
  }
}