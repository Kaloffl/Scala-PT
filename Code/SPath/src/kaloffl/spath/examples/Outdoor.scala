package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.PinholeCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.CheckeredMask
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.DirectionalSky
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.MaskedMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer

object Outdoor {

  def main(args: Array[String]): Unit = {

    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))

    val checkeredMask = new CheckeredMask(2)
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)

    val matWhiteLight = new LightMaterial(Color.White * 2)
    val matAir = new TransparentMaterial(
      color = Color(0.08f, 0.09f, 0.095f),
      scatterProbability = 0.001f)
    val matSky = new DirectionalSky(
      color = Color.White * 0.125f,
      direction = Vec3d.Down,
      limit = 1)

    val outdoor = SceneNode(Array(
      SceneNode(
        AABB(Vec3d(0, -0.25, 0), Vec3d(10, 0.5, 10)),
        matBlackWhiteCheckered),

      SceneNode(new Sphere(Vec3d(3, 1, -3), 1), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(3, 1, 3), 1), matWhiteDiffuse),

      SceneNode(new Sphere(Vec3d(0, 2, 0), 1), matWhiteLight),

      SceneNode(new Sphere(Vec3d(-3, 1, -3), 1), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(-3, 1, 3), 1), matWhiteDiffuse)))

    RenderEngine.render(
      bounces = 12,
      target = new JfxDisplay(1280, 720),
      tracer = new PathTracer(new Scene(
        root = outdoor,
        airMedium = matAir,
        skyMaterial = matSky,
        camera = new PinholeCamera(
          position = Vec3d(0, 2.5, 13),
          forward = Vec3d.Back,
          up = Vec3d.Up))))
  }
}