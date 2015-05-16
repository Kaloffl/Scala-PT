package kaloffl.spath.examples

import kaloffl.spath.Display
import kaloffl.spath.PathTracer
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial

object Refraction3 {
  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val glassColor = Color(0.2f, 0.4f, 0.5f)

    val matBlackDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = new LightMaterial(Color.WHITE, 2, 1024)

    val matAir = new TransparentMaterial(Color.WHITE, 0, 0.0, 1.0)

    val environment = Array(
      SceneNode(
        AABB(Vec3d(0, 16.5, 0), Vec3d(32, 1, 32)),
        matWhiteLight),

      SceneNode(
        AABB(Vec3d(0, -0.5, 0), Vec3d(32, 1, 32)),
        matWhiteDiffuse), //matBlackWhiteCheckered),
      SceneNode(
        AABB(Vec3d(16.5f, 8, 0), Vec3d(1, 16, 32)),
        matWhiteDiffuse), //matRedDiffuse),
      SceneNode(
        AABB(Vec3d(-16.5f, 8, 0), Vec3d(1, 16, 32)),
        matWhiteDiffuse), //matGreenDiffuse),
      SceneNode(
        AABB(Vec3d(0, 8, -16.5f), Vec3d(32, 16, 1)),
        matWhiteDiffuse),
      SceneNode(
        AABB(Vec3d(0, 8, 16.5), Vec3d(32, 16, 1)),
        matWhiteDiffuse))

    val minHeight = 1.0
    val maxHeight = 4.0
    val objects = (for (x ← 0 until 10; y ← 0 until 10) yield {
      SceneNode(
        new Sphere(Vec3d(x * 2 - 10, 0.501, y * 2 - 10), 0.5f),
        new TransparentMaterial(glassColor, Math.pow(2, x - 1), 0.0, 1 + y / 10.0))
    }).toArray

    val front = Vec3d(0, -11, 9)
    val up = front.cross(Vec3d.LEFT).normalize
    val camera = new Camera(Vec3d(0, 14, -14), front.normalize, up, 0.0, 3)

    val glassScene = new Scene(SceneNode(environment ++ objects), camera, matAir, matBlackDiffuse)

    pathTracer.render(display, glassScene, bounces = 12)
  }
}