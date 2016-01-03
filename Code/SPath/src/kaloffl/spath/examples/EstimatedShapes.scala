package kaloffl.spath.examples

import kaloffl.spath.scene.Scene
import kaloffl.spath.Display
import kaloffl.spath.PathTracer
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Color
import kaloffl.spath.scene.shapes.EstimatedSphere
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.shapes.Mandelbulb
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.MaskedMaterial
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.materials.ReflectiveMaterial
import kaloffl.spath.scene.materials.CheckeredMask
import kaloffl.spath.math.Attenuation

object EstimatedShapes {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val matPaper = new TransparentMaterial(Color(0.01f, 0.01f, 0.01f), 10, 500, 1.557, 0.01)

    val matRedDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = new LightMaterial(Color.WHITE, Attenuation.none)
    val matRedLight = new LightMaterial(Color.RED * 2, Attenuation.none)
    val matGreenLight = new LightMaterial(Color.GREEN * 2, Attenuation.none)

    val matMirror = ReflectiveMaterial(Color(0.5f, 0.5f, 0.5f), 0.001)

    val checkeredMask = new CheckeredMask(2, Vec3d(0.5))
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)

    val matAir = new TransparentMaterial(Color.BLACK, 0, 0.0, 1.0)

    val glassTest = SceneNode(Array(

      SceneNode(
        new Mandelbulb(Vec3d.ORIGIN, 8),
        matWhiteDiffuse),

      SceneNode(
        new Sphere(Vec3d(4, 0, 0), 1),
        matRedLight),
      SceneNode(
        new Sphere(Vec3d(-4, 0, 0), 1),
        matGreenLight),

      SceneNode(
        AABB(Vec3d(0, -1.5, 0), Vec3d(10, 1, 10)),
        matBlackWhiteCheckered),
      SceneNode(
        AABB(Vec3d(0, 7.5, 0), Vec3d(10, 1, 10)),
        matWhiteLight),
      SceneNode(
        AABB(Vec3d(5.5, 3, 0), Vec3d(1, 8, 10)),
        matWhiteDiffuse),
      SceneNode(
        AABB(Vec3d(0, 3, -5.5), Vec3d(10, 8, 1)),
        matWhiteDiffuse),
      SceneNode(
        AABB(Vec3d(-5.5, 3, 0), Vec3d(1, 8, 10)),
        matWhiteDiffuse),
      SceneNode(
        AABB(Vec3d(0, 3, 5.5), Vec3d(10, 8, 1)),
        matWhiteDiffuse)))

    val front = Vec3d(0, -1.5, -2.5).normalize
    val up = Vec3d.LEFT.cross(front)
    val camera = new Camera(Vec3d(0, 1.5, 2.5), front, up, 0.015, 3)

    val glassScene = new Scene(glassTest, camera, matAir, matBlackDiffuse)

    pathTracer.render(display, glassScene, bounces = 4)
  }
}