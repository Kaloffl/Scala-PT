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
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.structure.SceneNode

object Nested {

  def main(args: Array[String]): Unit = {

    val matAir = new TransparentMaterial(Color(0.2f, 0.1f, 0.05f), 0.1)
    val matSky = new LightMaterial(Color(1.0f, 0.95f, 0.9f) * 2, Attenuation.none)
    val matGlassRed = new TransparentMaterial(Color(0.1f, 0.9f, 0.9f), 2, 0, 1.3)
    val matGlassGreen = new TransparentMaterial(Color(0.9f, 0.1f, 0.9f), 2, 0, 1.4)
    val matGlassBlue = new TransparentMaterial(Color(0.9f, 0.9f, 0.1f), 2, 0, 1.5)
    val matFloor = DiffuseMaterial(Color(0.6f, 0.65f, 0.7f))

    val boxRed = SceneNode(AABB(Vec3d(0, 1.5, 0), Vec3d(3)), matGlassRed)
    val boxGreen = SceneNode(AABB(Vec3d(0, 1.5, 0), Vec3d(2)), matGlassGreen)
    val boxBlue = SceneNode(AABB(Vec3d(0, 1.5, 0), Vec3d(1)), matGlassBlue)

    val floor = SceneNode(AABB(Vec3d(0, -0.05, 0), Vec3d(8, 0.1, 8)), matFloor)

    val bunnyForward = Vec3d(-2, -1.75, -10)
    val bunnyTop = bunnyForward.cross(Vec3d.Right).normalize

    RenderEngine.render(
      bounces = 12,
      target = new Display(1280, 720),
      scene = new Scene(
        root = SceneNode(Array(floor, boxRed, boxGreen, boxBlue)),
        airMedium = matAir,
        skyMaterial = matSky,
        camera = new Camera(
          position = Vec3d(0, 4.5, 9),
          forward = bunnyForward.normalize,
          up = bunnyTop,
          aperture = 0.2,
          focalLength = bunnyForward.length)))
  }
}