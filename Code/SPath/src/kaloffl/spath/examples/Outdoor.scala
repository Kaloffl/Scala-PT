package kaloffl.spath.examples

import kaloffl.spath.scene.Scene
import kaloffl.spath.Display
import kaloffl.spath.PathTracer
import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Color
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.materials.DirectionalLightMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.MaskedMaterial
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.materials.CheckeredMask
import kaloffl.spath.math.Attenuation

object Outdoor {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val lowCamera = new Camera(Vec3d(0, 2.5, 13), Vec3d.BACK, Vec3d.UP, 0.0f, 13);

    val matWhiteDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matBlackDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))

    val checkeredMask = new CheckeredMask(2)
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)

    val matWhiteLight = new LightMaterial(Color.WHITE, 2, Attenuation.radius(1))
    val matAir = new TransparentMaterial(Color(0.8f, 0.9f, 0.95f), 0.1, 0.001, 1.0)

    val outdoor = SceneNode(Array(
      SceneNode(
        AABB(Vec3d(0, -0.25, 0), Vec3d(10, 0.5, 10)),
        matBlackWhiteCheckered),

      SceneNode(
        new Sphere(Vec3d(3, 1, -3), 1),
        matWhiteDiffuse),
      //      SceneNode(
      //        new Sphere(Vec3d(3, 1, 0), 1),
      //        matWhiteDiffuse),
      SceneNode(
        new Sphere(Vec3d(3, 1, 3), 1),
        matWhiteDiffuse),

      SceneNode(
        new Sphere(Vec3d(0, 2, 0), 1),
        matWhiteLight),

      SceneNode(
        new Sphere(Vec3d(-3, 1, -3), 1),
        matWhiteDiffuse),
      //      SceneNode(
      //        new Sphere(Vec3d(-3, 1, 0), 1),
      //        matWhiteDiffuse),
      SceneNode(
        new Sphere(Vec3d(-3, 1, 3), 1),
        matWhiteDiffuse)
      //      SceneNode(
        //        new Sphere(Vec3d(0, 1, 0), 1),
        //        matWhiteDiffuse)
        ))

    val outdoorScene = new Scene(outdoor, lowCamera, matAir, new DirectionalLightMaterial(Color.WHITE, 0.125f, Vec3d.DOWN, 1))

    pathTracer.render(display, outdoorScene, bounces = 12)
  }
}