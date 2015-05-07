package kaloffl.spath.examples

import kaloffl.spath.scene.Scene
import kaloffl.spath.PathTracer
import kaloffl.spath.Display
import kaloffl.spath.scene.Camera
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.importer.PlyImporter
import kaloffl.spath.scene.TransparentMaterial
import kaloffl.spath.scene.DiffuseMaterial
import kaloffl.spath.math.Color
import kaloffl.spath.scene.LightMaterial
import kaloffl.spath.scene.MaskedMaterial
import kaloffl.spath.scene.CheckeredMask

object Dragon {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val matRedDiffuse = new DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = new DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matYellowDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.1f))
    val matCyanDiffuse = new DiffuseMaterial(Color(0.1f, 0.9f, 0.9f))
    val matPinkDiffuse = new DiffuseMaterial(Color(0.9f, 0.1f, 0.9f))
    val matBlackDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matAir = new TransparentMaterial(Color(0.8f, 0.9f, 0.95f), 0.05, 0.0, 1.0)

    val matWhiteLight = new LightMaterial(Color.WHITE, 2, 1024)

    val matRedGlass = new TransparentMaterial(Color(0.2f, 0.4f, 0.5f), 16, 0, 1.5)

    val checkeredMask = new CheckeredMask(2)
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)

    val objects = Array(
      new SceneObject(
        new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2),
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
        PlyImporter.load("D:/temp/dragon.ply", Vec3d(40), Vec3d(-0.5, -2, 0)),
        matRedGlass),

      new SceneObject(
        AABB(Vec3d(0, -0.5, 4), Vec3d(16, 1, 24)),
        matBlackWhiteCheckered),
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

    val dragonForward = Vec3d(-1, -1.5, -4.5)
    val dragonTop = dragonForward.cross(Vec3d.RIGHT).normalize
    val dragonCam = new Camera(Vec3d(-1, 6, 4), dragonForward.normalize, dragonTop, 0.05, dragonForward.length)

    val dragonScene = new Scene(objects, dragonCam, matAir, matBlackDiffuse)

    pathTracer.render(display, dragonScene, bounces = 12)
  }
}