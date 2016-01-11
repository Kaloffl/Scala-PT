package kaloffl.spath.examples

import kaloffl.spath.Display
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.ReflectiveMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode

object Mirrored {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)

    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matMirror = ReflectiveMaterial(Color.WHITE, 0)
    val matAir = new TransparentMaterial(Color(0.2f, 0.1f, 0.05f), 0.1, 0.002, 1.0)

    val coloredSpheres = SceneNode(Array(
      SceneNode(
        new Sphere(Vec3d(0.0, 2.5, 0.0), 2.0f),
        DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))),

      SceneNode(
        AABB(Vec3d(0, -0.5, 0), Vec3d(16, 1, 24)),
        ReflectiveMaterial(Color(0.2f, 0.8f, 0.2f), 0.4f)),
      SceneNode(
        AABB(Vec3d(0, 32.5, 0), Vec3d(16, 1, 24)),
        new LightMaterial(Color.WHITE, Attenuation.none)),
      SceneNode(
        AABB(Vec3d(8.5f, 16, 0), Vec3d(1, 32, 24)),
        matMirror),
      SceneNode(
        AABB(Vec3d(-8.5f, 16, 0), Vec3d(1, 32, 24)),
        matMirror),
      SceneNode(
        AABB(Vec3d(0, 16, -12.5f), Vec3d(16, 32, 1)),
        matMirror),
      SceneNode(
        AABB(Vec3d(0, 16, 12.5), Vec3d(16, 32, 1)),
        matMirror)))

    val lowCamera = new Camera(Vec3d(0, 2.5, 10), Vec3d.BACK, Vec3d.UP, 0.03f, 10);

    val colorfulScene = new Scene(
        root = coloredSpheres, 
        camera = lowCamera, 
        airMedium = matAir, 
        skyMaterial = matBlackDiffuse)

    RenderEngine.render(target = display, scene = colorfulScene, bounces = 80)
  }
}