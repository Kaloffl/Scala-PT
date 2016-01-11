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
import kaloffl.spath.scene.materials.RefractiveMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode

object LightScatter {

  def main(args: Array[String]): Unit = {

    val display = new Display(1280, 720)

    val front = Vec3d(0, -2.5, -13).normalize
    val up = front.cross(Vec3d.RIGHT).normalize
    val camera = new Camera(Vec3d(0, 5, 13), front, up, 0.1, Vec3d(0, -2.5, -13).length)

    val matCyanDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.9f))
    val matPinkDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.9f))
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val checkeredMask = new CheckeredMask(2)
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)

    val matWhiteGlass8 = RefractiveMaterial(Color.WHITE, 1.8, 0.0)
    val matAir = new TransparentMaterial(Color(0.8f, 0.9f, 0.95f), 0.1, 0.05, 1.0)

    val hazeObjects = SceneNode(Array(

      SceneNode(
        new Sphere(Vec3d(0, 0, 0), 1),
        new LightMaterial(Color(1, 0.9f, 0.8f) * 4, Attenuation.radius(1))),

      SceneNode(
        AABB(Vec3d(0, 0, -1.05), Vec3d(2, 4, 0.1)),
        matBlackDiffuse),
      SceneNode(
        AABB(Vec3d(-1.05, 0, 0), Vec3d(0.1, 4, 2)),
        matBlackDiffuse),
      SceneNode(
        AABB(Vec3d(0, 0, 1.05), Vec3d(2, 4, 0.1)),
        matBlackDiffuse),
      SceneNode(
        AABB(Vec3d(1.05, 0, 0), Vec3d(0.1, 4, 2)),
        matBlackDiffuse)))

    val hazeScene = new Scene(
        root = hazeObjects, 
        camera = camera, 
        airMedium = matAir, 
        skyMaterial = matBlackDiffuse)
    RenderEngine.render(target = display, scene = hazeScene, bounces = 12)
  }
}