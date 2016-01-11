package kaloffl.spath.examples

import kaloffl.spath.Display
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.CheckeredMask
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.MaskedMaterial
import kaloffl.spath.scene.materials.ReflectiveMaterial
import kaloffl.spath.scene.materials.RefractiveMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode

object Scatter5 {
  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)

    val matPaper = new TransparentMaterial(Color(0.01f, 0.01f, 0.01f), 10, 500, 1.557, 0.01)

    val matRedDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = new LightMaterial(Color.WHITE * 16, Attenuation.none)
    val matSkyLight = new LightMaterial(Color(0.9f, 0.95f, 0.975f) * 0.5f, Attenuation.none)
    val matRedLight = new LightMaterial(Color.RED * 256, Attenuation.none)
    val matGreenLight = new LightMaterial(Color.GREEN * 16, Attenuation.none)

    val matRedGlass = new TransparentMaterial(Color(0.1f, 0.5f, 0.5f), 1, 0.1, 1.7)
    val matClearGlass = RefractiveMaterial(Color.WHITE, 1.7, 0.0)
    val matWhiteGlass = new TransparentMaterial(Color.BLACK, 0.0, 0.1, 1.7)

    val matMirror = ReflectiveMaterial(Color.WHITE, 0.0001)

    val checkeredMask = new CheckeredMask(2, Vec3d(0.5))
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)

    val matAir = new TransparentMaterial(Color.BLACK, 0, 0.0, 1.0)

    val glassTest = SceneNode(Array(
      SceneNode(
        new Sphere(Vec3d(0, 80, 0), 20),
        matWhiteLight),

      SceneNode(
        new Sphere(Vec3d(-20, 10, -10), 10),
        matMirror),

      SceneNode(
        new Sphere(Vec3d(-10, 5, 20), 5),
        matWhiteGlass),

      SceneNode(
        Array[Shape](
          new Sphere(Vec3d(15, 10, 0), 10),
          new Sphere(Vec3d(15, 25, 0), 5),
          new Sphere(Vec3d(15, 32.5, 0), 2.5f),
          new Sphere(Vec3d(15, 36.25, 0), 1.25f),
          new Sphere(Vec3d(15, 38.125, 0), 0.625f)),
        matRedGlass),

      SceneNode(
        Array[Shape](
          new Sphere(Vec3d(1, 2.5, -6), 2.5f),
          new Sphere(Vec3d(-10, 2.5, -4), 2.5f),
          new Sphere(Vec3d(0, 1.25, 0), 1.25f),
          new Sphere(Vec3d(-4, 1.25, 3), 1.25f)),
        matClearGlass),

      SceneNode(
        AABB(Vec3d(0, -0.5, 0), Vec3d(10000, 1, 10000)),
        matWhiteDiffuse)))

    val front = Vec3d(0, -1, -1).normalize
    val up = Vec3d.LEFT.cross(front)
    val camera = new Camera(Vec3d(0, 60, 60), front, up, 0.0, 9)

    val glassScene = new Scene(
        root = glassTest, 
        camera = camera, 
        airMedium = matAir, 
        skyMaterial = matSkyLight)

    RenderEngine.render(
        target = display, 
        scene = glassScene, 
        passes = 6000, 
        bounces = 64)
  }
}