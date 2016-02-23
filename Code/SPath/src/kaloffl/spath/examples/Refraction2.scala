package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.LensCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.CheckeredMask
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.MaskedMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer

object Refraction2 {
  def main(args: Array[String]): Unit = {

    val glassColor = Color(0.2f, 0.4f, 0.5f) * 16
    val scatter = 0.0
    val matGlass = Array.tabulate(10)(i => {
      new TransparentMaterial(color = glassColor, scatterProbability = scatter, refractiveIndex = 1 + (i + 1) / 10f)
    })
    val matRedDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = new LightMaterial(Color.White * 2)

    val checkeredMask = new CheckeredMask(2, Vec3d(0.5))
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)

    val glassTest = SceneNode(Array(
      SceneNode(new Sphere(Vec3d(-8.9, 1, 0.5), 1), matGlass(0)),
      SceneNode(new Sphere(Vec3d(-6.95, 1, 0.1), 1), matGlass(1)),
      SceneNode(new Sphere(Vec3d(-5, 1, -0.1), 1), matGlass(2)),
      SceneNode(new Sphere(Vec3d(-3, 1, -0.2), 1), matGlass(3)),
      SceneNode(new Sphere(Vec3d(-1, 1, -0.25), 1), matGlass(4)),
      SceneNode(new Sphere(Vec3d(1, 1, -0.25), 1), matGlass(5)),
      SceneNode(new Sphere(Vec3d(3, 1, -0.2), 1), matGlass(6)),
      SceneNode(new Sphere(Vec3d(5, 1, -0.1), 1), matGlass(7)),
      SceneNode(new Sphere(Vec3d(6.95, 1, 0.1), 1), matGlass(8)),
      SceneNode(new Sphere(Vec3d(8.9, 1, 0.5), 1), matGlass(9)),

      SceneNode(AABB(Vec3d(-9, 4, 2), Vec3d(1.5, 1.5, 0.2)), matGlass(0)),
      SceneNode(AABB(Vec3d(-7, 4, 2), Vec3d(1.5, 1.5, 0.2)), matGlass(1)),
      SceneNode(AABB(Vec3d(-5, 4, 2), Vec3d(1.5, 1.5, 0.2)), matGlass(2)),
      SceneNode(AABB(Vec3d(-3, 4, 2), Vec3d(1.5, 1.5, 0.2)), matGlass(3)),
      SceneNode(AABB(Vec3d(-1, 4, 2), Vec3d(1.5, 1.5, 0.2)), matGlass(4)),
      SceneNode(AABB(Vec3d(1, 4, 2), Vec3d(1.5, 1.5, 0.2)), matGlass(5)),
      SceneNode(AABB(Vec3d(3, 4, 2), Vec3d(1.5, 1.5, 0.2)), matGlass(6)),
      SceneNode(AABB(Vec3d(5, 4, 2), Vec3d(1.5, 1.5, 0.2)), matGlass(7)),
      SceneNode(AABB(Vec3d(7, 4, 2), Vec3d(1.5, 1.5, 0.2)), matGlass(8)),
      SceneNode(AABB(Vec3d(9, 4, 2), Vec3d(1.5, 1.5, 0.2)), matGlass(9)),

      SceneNode(AABB(Vec3d(0, 8.5, 4), Vec3d(20, 1, 24)), matWhiteLight),

      SceneNode(AABB(Vec3d(0, -0.5, 4), Vec3d(20, 1, 24)), matBlackWhiteCheckered),
      SceneNode(AABB(Vec3d(10.5f, 4, 4), Vec3d(1, 8, 24)), matRedDiffuse),
      SceneNode(AABB(Vec3d(-10.5f, 4, 4), Vec3d(1, 8, 24)), matBlueDiffuse),
      SceneNode(AABB(Vec3d(0, 4, -8.5f), Vec3d(20, 8, 1)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(0, 4, 16.5), Vec3d(20, 8, 1)), matWhiteDiffuse)))

    val front = Vec3d(0, -2.5, -13)
    val up = front.normalize.cross(Vec3d.Right).normalize

    RenderEngine.render(
      bounces = 12,
      target = new JfxDisplay(1280, 720),
      tracer = new PathTracer(new Scene(
        root = glassTest,
        camera = new LensCamera(
          position = Vec3d(0, 5, 13),
          forward = front.normalize,
          up = up,
          lensRadius = 0.1f,
          focussedDepth = front.length.toFloat))))
  }
}