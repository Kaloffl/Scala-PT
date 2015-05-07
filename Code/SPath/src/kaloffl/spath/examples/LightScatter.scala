package kaloffl.spath.examples

import kaloffl.spath.scene.Scene
import kaloffl.spath.PathTracer
import kaloffl.spath.Display
import kaloffl.spath.scene.Camera
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.LightMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.math.Color
import kaloffl.spath.scene.TransparentMaterial
import kaloffl.spath.scene.RefractiveMaterial
import kaloffl.spath.scene.DiffuseMaterial
import kaloffl.spath.scene.MaskedMaterial
import kaloffl.spath.scene.CheckeredMask

object LightScatter {

  def main(args: Array[String]): Unit = {

    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val front = Vec3d(0, -2.5, -13).normalize
    val up = front.cross(Vec3d.RIGHT).normalize
    val camera = new Camera(Vec3d(0, 5, 13), front, up, 0.1, Vec3d(0, -2.5, -13).length)

    val matCyanDiffuse = new DiffuseMaterial(Color(0.1f, 0.9f, 0.9f))
    val matPinkDiffuse = new DiffuseMaterial(Color(0.9f, 0.1f, 0.9f))
    val matBlackDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val checkeredMask = new CheckeredMask(2)
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)

    val matWhiteGlass8 = new RefractiveMaterial(Color.WHITE, 1.8, 0.0)
    val matAir = new TransparentMaterial(Color(0.8f, 0.9f, 0.95f), 0.1, 0.05, 1.0)

    val hazeObjects = Array(

      new SceneObject(
        new Sphere(Vec3d(0, 0, 0), 1),
        new LightMaterial(Color(1, 0.9f, 0.8f), 4, 1024)),

      new SceneObject(
        AABB(Vec3d(0, 0, -1.05), Vec3d(2, 4, 0.1)),
        matBlackDiffuse),
      new SceneObject(
        AABB(Vec3d(-1.05, 0, 0), Vec3d(0.1, 4, 2)),
        matBlackDiffuse),
      new SceneObject(
        AABB(Vec3d(0, 0, 1.05), Vec3d(2, 4, 0.1)),
        matBlackDiffuse),
      new SceneObject(
        AABB(Vec3d(1.05, 0, 0), Vec3d(0.1, 4, 2)),
        matBlackDiffuse))

    val hazeScene = new Scene(hazeObjects, camera, matAir, matBlackDiffuse)
    pathTracer.render(display, hazeScene, bounces = 12)
  }
}