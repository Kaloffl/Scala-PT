package kaloffl.spath.examples

import kaloffl.spath.Display
import kaloffl.spath.PathTracer
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.MaskedMaterial
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.materials.CheckeredMask
import kaloffl.spath.math.Attenuation

object Refraction2 {
  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val glassColor = Color(0.2f, 0.4f, 0.5f)
    val absorbtion = 16
    val scatter = 0.0
    val matGlass1 = new TransparentMaterial(glassColor, absorbtion, scatter, 1.1)
    val matGlass2 = new TransparentMaterial(glassColor, absorbtion, scatter, 1.2)
    val matGlass3 = new TransparentMaterial(glassColor, absorbtion, scatter, 1.3)
    val matGlass4 = new TransparentMaterial(glassColor, absorbtion, scatter, 1.4)
    val matGlass5 = new TransparentMaterial(glassColor, absorbtion, scatter, 1.5)
    val matGlass6 = new TransparentMaterial(glassColor, absorbtion, scatter, 1.6)
    val matGlass7 = new TransparentMaterial(glassColor, absorbtion, scatter, 1.7)
    val matGlass8 = new TransparentMaterial(glassColor, absorbtion, scatter, 1.8)
    val matGlass9 = new TransparentMaterial(glassColor, absorbtion, scatter, 1.9)
    val matGlassA = new TransparentMaterial(glassColor, absorbtion, scatter, 2.0)

    val matRedDiffuse = new DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = new DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matBlackDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = new LightMaterial(Color.WHITE, 2, Attenuation.none)

    val checkeredMask = new CheckeredMask(2, Vec3d(0.5))
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)

    val matAir = new TransparentMaterial(Color.BLACK, 0, 0.0, 1.0)

    val glassTest = SceneNode(Array(
      SceneNode(
        new Sphere(Vec3d(-8.9, 1, 0.5), 1),
        matGlass1),
      SceneNode(
        new Sphere(Vec3d(-6.95, 1, 0.1), 1),
        matGlass2),
      SceneNode(
        new Sphere(Vec3d(-5, 1, -0.1), 1),
        matGlass3),
      SceneNode(
        new Sphere(Vec3d(-3, 1, -0.2), 1),
        matGlass4),
      SceneNode(
        new Sphere(Vec3d(-1, 1, -0.25), 1),
        matGlass5),
      SceneNode(
        new Sphere(Vec3d(1, 1, -0.25), 1),
        matGlass6),
      SceneNode(
        new Sphere(Vec3d(3, 1, -0.2), 1),
        matGlass7),
      SceneNode(
        new Sphere(Vec3d(5, 1, -0.1), 1),
        matGlass8),
      SceneNode(
        new Sphere(Vec3d(6.95, 1, 0.1), 1),
        matGlass9),
      SceneNode(
        new Sphere(Vec3d(8.9, 1, 0.5), 1),
        matGlassA),

      SceneNode(
        AABB(Vec3d(-9, 4, 2), Vec3d(1.5, 1.5, 0.2)),
        matGlass1),
      SceneNode(
        AABB(Vec3d(-7, 4, 2), Vec3d(1.5, 1.5, 0.2)),
        matGlass2),
      SceneNode(
        AABB(Vec3d(-5, 4, 2), Vec3d(1.5, 1.5, 0.2)),
        matGlass3),
      SceneNode(
        AABB(Vec3d(-3, 4, 2), Vec3d(1.5, 1.5, 0.2)),
        matGlass4),
      SceneNode(
        AABB(Vec3d(-1, 4, 2), Vec3d(1.5, 1.5, 0.2)),
        matGlass5),
      SceneNode(
        AABB(Vec3d(1, 4, 2), Vec3d(1.5, 1.5, 0.2)),
        matGlass6),
      SceneNode(
        AABB(Vec3d(3, 4, 2), Vec3d(1.5, 1.5, 0.2)),
        matGlass7),
      SceneNode(
        AABB(Vec3d(5, 4, 2), Vec3d(1.5, 1.5, 0.2)),
        matGlass8),
      SceneNode(
        AABB(Vec3d(7, 4, 2), Vec3d(1.5, 1.5, 0.2)),
        matGlass9),
      SceneNode(
        AABB(Vec3d(9, 4, 2), Vec3d(1.5, 1.5, 0.2)),
        matGlassA),

      SceneNode(
        AABB(Vec3d(0, 8.5, 4), Vec3d(20, 1, 24)),
        matWhiteLight),

      SceneNode(
        AABB(Vec3d(0, -0.5, 4), Vec3d(20, 1, 24)),
        matBlackWhiteCheckered),
      SceneNode(
        AABB(Vec3d(10.5f, 4, 4), Vec3d(1, 8, 24)),
        matRedDiffuse),
      SceneNode(
        AABB(Vec3d(-10.5f, 4, 4), Vec3d(1, 8, 24)),
        matBlueDiffuse),
      SceneNode(
        AABB(Vec3d(0, 4, -8.5f), Vec3d(20, 8, 1)),
        matWhiteDiffuse),//matGreenDiffuse),
      SceneNode(
        AABB(Vec3d(0, 4, 16.5), Vec3d(20, 8, 1)),
        matWhiteDiffuse)))

    val front = Vec3d(0, -2.5, -13).normalize
    val up = front.cross(Vec3d.RIGHT).normalize
    val camera = new Camera(Vec3d(0, 5, 13), front, up, 0.1, Vec3d(0, -2.5, -13).length)

    val glassScene = new Scene(glassTest, camera, matAir, matBlackDiffuse)

    pathTracer.render(display, glassScene, bounces = 12)
  }
}