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

object Haze {

  def main(args: Array[String]): Unit = {

    val matCyanDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.9f))
    val matPinkDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.9f))
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val checkeredMask = new CheckeredMask(2)
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)

    val matWhiteGlass8 = RefractiveMaterial(
      color = Color.White,
      refractiveIndex = 1.8f)
    val matAir = new TransparentMaterial(
      color = Color(0.08f, 0.09f, 0.095f),
      scatterProbability = 0.001)
    val matLight = new LightMaterial(Color(1, 0.9f, 0.8f) * 2, Attenuation.radius(1))

    val hazeObjects = SceneNode(Array(
      SceneNode(new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f), matWhiteGlass8),
      SceneNode(new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f), matCyanDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f), matPinkDiffuse),
      SceneNode(new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f), matBlackDiffuse),

      SceneNode(new Sphere(Vec3d(0, 4, 0), 1), matLight),

      SceneNode(AABB(Vec3d(0, -0.5, 0), Vec3d(24, 1, 24)), matBlackWhiteCheckered)))

    val front = Vec3d(0, -2.5, -13).normalize
    val up = front.cross(Vec3d.Right).normalize

    RenderEngine.render(
      bounces = 12,
      target = new Display(1280, 720),
      scene = new Scene(
        root = hazeObjects,
        airMedium = matAir,
        skyMaterial = matBlackDiffuse,
        camera = new Camera(
          position = Vec3d(0, 5, 13),
          forward = front,
          up = up,
          aperture = 0.1,
          focalLength = Vec3d(0, -2.5, -13).length)))
  }
}