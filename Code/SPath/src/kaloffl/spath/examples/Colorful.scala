package kaloffl.spath.examples

import kaloffl.spath.filter.ScaleFilter
import kaloffl.spath.math.Units._
import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.sampler.SphereSampler
import kaloffl.spath.scene.materials.{DielectricMaterial, EmittingMaterial, TransparentMaterial}
import kaloffl.spath.scene.shapes.{Plane, Sphere}
import kaloffl.spath.scene.structure.{BoundlessNode, SceneNode}
import kaloffl.spath.scene.{GlobalHint, LensCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.RecursivePathTracer
import kaloffl.spath.{JfxDisplay, RenderEngine}

object Colorful {

  def main(args: Array[String]): Unit = {

    val matRedDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.9f, 0.1f, 0.1f), roughness = (_, _) => 0.5f)
    val matGreenDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.1f, 0.9f, 0.1f), roughness = (_, _) => 0.5f)
    val matBlueDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.1f, 0.1f, 0.9f), roughness = (_, _) => 0.5f)
    val matYellowDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.9f, 0.9f, 0.1f), roughness = (_, _) => 0.25f, ior = 2)
    val matCyanDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.1f, 0.9f, 0.9f), roughness = (_, _) => 0.5f)
    val matPinkDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.9f, 0.1f, 0.9f), roughness = (_, _) => 0.5f)
    val matBlackDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.1f, 0.1f, 0.1f), roughness = (_, _) => 0.5f)
    val matWhiteDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.9f, 0.9f, 0.9f), roughness = (_, _) => 0.5f)

    val matGlass = new TransparentMaterial(ior = 1.4f)

    val matLight = new EmittingMaterial(Color.White, 8)

    val light1 = new Sphere(Vec3d(-4, 7.5, -4), 0.5f)
    val light2 = new Sphere(Vec3d(-4, 7.5, 0), 0.5f)
    val light3 = new Sphere(Vec3d(-4, 7.5, 4), 0.5f)
    val light4 = new Sphere(Vec3d(0, 7.5, -4), 0.5f)
    val light5 = new Sphere(Vec3d(0, 7.5, 0), 0.5f)
    val light6 = new Sphere(Vec3d(0, 7.5, 4), 0.5f)
    val light7 = new Sphere(Vec3d(4, 7.5, -4), 0.5f)
    val light8 = new Sphere(Vec3d(4, 7.5, 0), 0.5f)
    val light9 = new Sphere(Vec3d(4, 7.5, 4), 0.5f)

    val coloredSpheres = BoundlessNode(Array(
      SceneNode(new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f), matYellowDiffuse),
      SceneNode(new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f), matCyanDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f), matPinkDiffuse),
      SceneNode(new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f), matWhiteDiffuse),
      //SceneNode(new Sphere(Vec3d(0.0f, 2.0f, 5.0f), 1.0f), matGlass),

      SceneNode(light1, matLight),
      //SceneNode(light2, matLight),
      SceneNode(light3, matLight),
      //SceneNode(light4, matLight),
      //SceneNode(light5, matLight),
      //SceneNode(light6, matLight),
      SceneNode(light7, matLight),
      //SceneNode(light8, matLight),
      SceneNode(light9, matLight),

      BoundlessNode(new Plane(Vec3d.Up, 0), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Down, 8), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Right, 8), matBlueDiffuse),
      BoundlessNode(new Plane(Vec3d.Left, 8), matRedDiffuse),
      BoundlessNode(new Plane(Vec3d.Front, 8), matGreenDiffuse),
      BoundlessNode(new Plane(Vec3d.Back, 16), matWhiteDiffuse)))

    val window = new JfxDisplay(1280, 720)

    RenderEngine.render(
      //target = window,
      target = new ScaleFilter(window, 4, 4),
      //tracer = new PathTracer(maxBounces = 8),
      tracer = new RecursivePathTracer(maxBounces = 4),
      //tracer = new NaivePathTracer(maxBounces = 4),
      cpuSaturation = 0.5f,
      view = new Viewpoint(
        position = Vec3d(0, 2.5, 13),
        forward = Vec3d.Back,
        up = Vec3d.Up),
      scene = new Scene(
        root = coloredSpheres,
        lightHints = Array(
          GlobalHint(new SphereSampler(light1)),
          GlobalHint(new SphereSampler(light3)),
          GlobalHint(new SphereSampler(light7)),
          GlobalHint(new SphereSampler(light9))),
        camera = new LensCamera(
            lensRadius = mm(7),
            focussedDepth = 8)))
  }
}