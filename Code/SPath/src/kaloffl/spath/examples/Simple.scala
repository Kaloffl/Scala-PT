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
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode

object Simple {

  def main(args: Array[String]): Unit = {

    val matRedDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matAir = new TransparentMaterial(Color(0.2f, 0.1f, 0.05f), 0.0, 0.0, 1.0)

    val matMirror = ReflectiveMaterial(Color.White, 0.0)
    val matGlass = new TransparentMaterial(Color(0.9f, 0.9f, 0.9f), 0.1, 0.0, 2.0, 0.0)
    val matLight = new LightMaterial(Color.White * 8f, Attenuation.radius(1f))

    val coloredSpheres = SceneNode(Array(
      SceneNode(new Sphere(Vec3d(-5.0f, 1.5f, -1.5f), 1.5f), matMirror),
      SceneNode(new Sphere(Vec3d(-1.0f, 1.5f, 0.0f), 1.5f), matGlass),
      SceneNode(new Sphere(Vec3d(-3.0f, 1f, 2.0f), 1f), matWhiteDiffuse),

      SceneNode(
        Array[Shape](
          new Sphere(Vec3d(-3.5f, 5.0f, -1.0f), 1f),
          new Sphere(Vec3d(-1.0f, 3.0f, -5.9f), 1f)),
        matLight),

      SceneNode(AABB(Vec3d(0, -0.5, 0), Vec3d(16, 1, 16)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(0, 16.5, 0), Vec3d(16, 1, 16)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(8.5f, 8, 0), Vec3d(1, 16, 16)), matBlueDiffuse),
      SceneNode(AABB(Vec3d(-8.5f, 8, 0), Vec3d(1, 16, 16)), matRedDiffuse),
      SceneNode(AABB(Vec3d(0, 8, -8.5f), Vec3d(16, 16, 1)), matGreenDiffuse),
      SceneNode(AABB(Vec3d(0, 8, 8.5), Vec3d(16, 16, 1)), matBlackDiffuse)))

    val focus = Vec3d(-4, 0.5, -1)
    val position = Vec3d(3, 5.5, 6)
    val forward = (focus - position).normalize
    val up = Vec3d(1, 0, -1).normalize.cross(forward).normalize

    RenderEngine.render(
      bounces = 12,
      target = new Display(1280, 720),
      scene =
        new Scene(
          root = coloredSpheres,
          airMedium = matAir,
          skyMaterial = matBlackDiffuse,
          camera = new Camera(
            position = position,
            forward = forward,
            up = up)))
  }
}