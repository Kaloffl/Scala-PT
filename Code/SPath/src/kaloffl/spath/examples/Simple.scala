package kaloffl.spath.examples

import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.sampler.SphereSampler
import kaloffl.spath.scene.materials.{DiffuseMaterial, EmittingMaterial, MetalMaterial, TransparentMaterial}
import kaloffl.spath.scene.shapes.{AABB, Sphere}
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.{GlobalHint, PinholeCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.RecursivePathTracer
import kaloffl.spath.{JfxDisplay, RenderEngine}

object Simple {

  def main(args: Array[String]): Unit = {

    val matRedDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matMirror = new MetalMaterial((_, _) => Color.White)
    val matGlass = new TransparentMaterial(
      volumeColor = Color(0.09f, 0.09f, 0.09f),
      ior = 2.0f)
    val matLight = new EmittingMaterial(Color.White, 8f)

    val light1 = new Sphere(Vec3d(-3.5f, 5.0f, -1.0f), 1f)
    val light2 = new Sphere(Vec3d(-1.0f, 3.0f, -5.9f), 1f)

    val coloredSpheres = SceneNode(Array(
      SceneNode(new Sphere(Vec3d(-5.0f, 1.5f, -1.5f), 1.5f), matMirror),
      SceneNode(new Sphere(Vec3d(-1.0f, 1.5f, 0.0f), 1.5f), matGlass),
      SceneNode(new Sphere(Vec3d(-3.0f, 1f, 2.0f), 1f), matWhiteDiffuse),

      SceneNode(Array(light1, light2), matLight),

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
      target = new JfxDisplay(1280, 720),
      tracer = new RecursivePathTracer(maxBounces = 5),
      view = new Viewpoint(
        position = position,
        forward = forward,
        up = up),
      scene = new Scene(
        root = coloredSpheres,
        lightHints = Array(
          GlobalHint(new SphereSampler(light1)),
          GlobalHint(new SphereSampler(light2))),
        camera = new PinholeCamera))
  }
}