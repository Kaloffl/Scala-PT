package kaloffl.spath.examples

import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.math.Vec3d
import kaloffl.spath.PathTracer
import kaloffl.spath.Display
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.RefractiveMaterial
import kaloffl.spath.math.Color
import kaloffl.spath.scene.TransparentMaterial
import kaloffl.spath.scene.DiffuseMaterial
import kaloffl.spath.scene.LightMaterial
import kaloffl.spath.scene.MaskedMaterial
import kaloffl.spath.scene.CheckeredMask

object Refraction {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

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

    val matRedDiffuse = new DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = new DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matBlackDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = new LightMaterial(Color.WHITE, 2, 1024)

    val checkeredMask = new CheckeredMask(2)
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)

    val matAir = new TransparentMaterial(Color.WHITE, 0.1, 0.0, 1.0)

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
        AABB(Vec3d(-9, 1, 4), Vec3d(1.5)),
        matWhiteGlass0),
      new SceneObject(
        AABB(Vec3d(-7, 1, 4), Vec3d(1.5)),
        matWhiteGlass1),
      new SceneObject(
        AABB(Vec3d(-5, 1, 4), Vec3d(1.5)),
        matWhiteGlass2),
      new SceneObject(
        AABB(Vec3d(-3, 1, 4), Vec3d(1.5)),
        matWhiteGlass3),
      new SceneObject(
        AABB(Vec3d(-1, 1, 4), Vec3d(1.5)),
        matWhiteGlass4),
      new SceneObject(
        AABB(Vec3d(1, 1, 4), Vec3d(1.5)),
        matWhiteGlass5),
      new SceneObject(
        AABB(Vec3d(3, 1, 4), Vec3d(1.5)),
        matWhiteGlass6),
      new SceneObject(
        AABB(Vec3d(5, 1, 4), Vec3d(1.5)),
        matWhiteGlass7),
      new SceneObject(
        AABB(Vec3d(7, 1, 4), Vec3d(1.5)),
        matWhiteGlass8),
      new SceneObject(
        AABB(Vec3d(9, 1, 4), Vec3d(1.5)),
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

    val front = Vec3d(0, -2.5, -13).normalize
    val up = front.cross(Vec3d.RIGHT).normalize
    val camera = new Camera(Vec3d(0, 5, 13), front, up, 0.1, Vec3d(0, -2.5, -13).length)

    val glassScene = new Scene(glassTest, camera, matAir, matBlackDiffuse)

    pathTracer.render(display, glassScene, bounces = 12)
  }
}