package kaloffl.spath.examples

import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.math.Vec3d
import kaloffl.spath.PathTracer
import java.util.function.DoubleSupplier
import kaloffl.spath.Display
import java.util.concurrent.ThreadLocalRandom
import kaloffl.spath.scene.TransparentMaterial
import kaloffl.spath.math.Color
import kaloffl.spath.scene.DiffuseMaterial
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.LightMaterial

object CornellBox {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val front = Vec3d(0, -2.5, -13).normalize
    val up = front.cross(Vec3d.RIGHT).normalize
    val cornellCam = new Camera(Vec3d(-3, 5, 13), front, up, 0.01f, 13.0f)

    val matAir = new TransparentMaterial(Color(0.8f, 0.9f, 0.95f), 0.1, 0.001, 1.0)
    val matBlackDiffuse = new DiffuseMaterial(Color.BLACK)
    val cmatWhite = new DiffuseMaterial(Color(0.76f, 0.75f, 0.5f))
    val cmatRed = new DiffuseMaterial(Color(0.63f, 0.06f, 0.04f))
    val cmatGreen = new DiffuseMaterial(Color(0.15f, 0.48f, 0.09f))
    val cmatLight = new LightMaterial(Color.WHITE, 1, 512)

    val cornellBox = Array(
      new SceneObject(
        new Sphere(Vec3d(0, 17, 0), 10),
        cmatLight),

      //            new SceneObject(
      //              PlyImporter.load("D:/temp/bunny_flipped.ply", Vec3d(30), Vec3d(-4, -0.989622, 0)),
      //              cmatWhite),

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

    val cornellScene = new Scene(cornellBox, cornellCam, matAir, matBlackDiffuse)

    pathTracer.render(display, cornellScene, bounces = 12)
  }
}