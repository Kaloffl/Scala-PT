package kaloffl.spath.examples

import kaloffl.spath.scene.LightMaterial
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.DiffuseMaterial
import kaloffl.spath.Display
import kaloffl.spath.PathTracer
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.TransparentMaterial
import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Color
import kaloffl.spath.scene.ReflectiveMaterial

object Mirrored {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val matBlackDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matMirror = new ReflectiveMaterial(Color.WHITE, 0)
    val matAir = new TransparentMaterial(Color(0.2f, 0.1f, 0.05f), 0.1, 0.002, 1.0)

    val coloredSpheres = Array(
      new SceneObject(
        new Sphere(Vec3d(0.0, 2.5, 0.0), 2.0f),
        new DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))),

      new SceneObject(
        AABB(Vec3d(0, -0.5, 0), Vec3d(16, 1, 24)),
        new ReflectiveMaterial(Color(0.2f, 0.8f, 0.2f), 0.4f)),
      new SceneObject(
        AABB(Vec3d(0, 32.5, 0), Vec3d(16, 1, 24)),
        new LightMaterial(Color.WHITE, 1f, 1024f)),
      new SceneObject(
        AABB(Vec3d(8.5f, 16, 0), Vec3d(1, 32, 24)),
        matMirror),
      new SceneObject(
        AABB(Vec3d(-8.5f, 16, 0), Vec3d(1, 32, 24)),
        matMirror),
      new SceneObject(
        AABB(Vec3d(0, 16, -12.5f), Vec3d(16, 32, 1)),
        matMirror),
      new SceneObject(
        AABB(Vec3d(0, 16, 12.5), Vec3d(16, 32, 1)),
        matMirror))

    val lowCamera = new Camera(Vec3d(0, 2.5, 10), Vec3d.BACK, Vec3d.UP, 0.03f, 10);

    val colorfulScene = new Scene(coloredSpheres, lowCamera, matAir, matBlackDiffuse)

    pathTracer.render(display, colorfulScene, bounces = 80)
  }
}