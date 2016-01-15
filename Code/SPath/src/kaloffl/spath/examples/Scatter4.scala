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
import kaloffl.spath.scene.structure.SceneNode

object Scatter4 {
  def main(args: Array[String]): Unit = {

    val matPaper = new TransparentMaterial(Color(0.01f, 0.01f, 0.01f), 10, 500, 1.557, 0.01)
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matWhiteLight = new LightMaterial(Color.White * 16, Attenuation.none)
    val matRedLight = new LightMaterial(Color.Red * 256, Attenuation.none)
    val matGreenLight = new LightMaterial(Color.Green * 16, Attenuation.none)
    val matMirror = ReflectiveMaterial(Color(0.5f, 0.5f, 0.5f), 0.001)
    val matAir = new TransparentMaterial(Color.Black, 0, 0.0, 1.0)
    val matSky = new LightMaterial(Color.White, Attenuation.none)

    val glassTest = SceneNode(Array(
      SceneNode(AABB(Vec3d(0, 4.5, 0), Vec3d(10, 3, 0.001)), matPaper),
      SceneNode(AABB(Vec3d(0, 1.5, 0), Vec3d(10, 3, 0.001)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(-3, 3, -2), Vec3d(1, 1, 0.1)), matRedLight),
      SceneNode(AABB(Vec3d(3, 3, 2), Vec3d(1, 1, 0.1)), matGreenLight),
      SceneNode(AABB(Vec3d(0, -0.5, 0), Vec3d(10, 1, 10)), matBlackDiffuse)))

    val front = Vec3d(0, 0, -9).normalize

    RenderEngine.render(
      bounces = 128,
      target = new Display(1280, 720),
      scene = new Scene(
        root = glassTest,
        airMedium = matAir,
        skyMaterial = matSky,
        camera = new Camera(
          position = Vec3d(0, 3, 9),
          forward = front,
          up = Vec3d.Up,
          aperture = 0.01,
          focalLength = 9)))
  }
}