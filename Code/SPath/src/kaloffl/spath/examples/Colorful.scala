package kaloffl.spath.examples

import kaloffl.spath.math.Units._
import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.scene.materials.{DielectricMaterial, EmittingMaterial, TransparentMaterial}
import kaloffl.spath.scene.shapes.{Plane, Sphere}
import kaloffl.spath.scene.structure.{BoundlessNode, SceneNode}
import kaloffl.spath.scene.{LensCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.BidirectionalPathTracer
import kaloffl.spath.{JfxDisplay, RenderEngine}

object Colorful {

  def main(args: Array[String]): Unit = {

    val matRedDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.9f, 0.1f, 0.1f), roughness = (_, _) => 0.5f)
    val matGreenDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.1f, 0.9f, 0.1f), roughness = (_, _) => 0.5f)
    val matBlueDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.1f, 0.1f, 0.9f), roughness = (_, _) => 0.5f)
    val matYellowDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.9f, 0.9f, 0.1f), roughness = (_, _) => 0.5f)
    val matCyanDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.1f, 0.9f, 0.9f), roughness = (_, _) => 0.5f)
    val matPinkDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.9f, 0.1f, 0.9f), roughness = (_, _) => 0.5f)
    val matBlackDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.1f, 0.1f, 0.1f), roughness = (_, _) => 0.5f)
    val matWhiteDiffuse = new DielectricMaterial(albedo = (_, _) => Color(0.9f, 0.9f, 0.9f), roughness = (_, _) => 0.5f)

    val matGlass = new TransparentMaterial(ior = new Color(1.4f, 1.5f, 1.6f))

    val matLight = new EmittingMaterial(Color.White, 3)

    val coloredSpheres = BoundlessNode(Array(
      SceneNode(new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f), matYellowDiffuse),
      SceneNode(new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f), matCyanDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f), matPinkDiffuse),
      SceneNode(new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(0.0f, 2.0f, 5.0f), 1.0f), matGlass),

      SceneNode(new Sphere(Vec3d(-4, 7.5, -4), 0.5f), matLight),
      SceneNode(new Sphere(Vec3d(-4, 7.5, 0), 0.5f), matLight),
      SceneNode(new Sphere(Vec3d(-4, 7.5, 4), 0.5f), matLight),
      SceneNode(new Sphere(Vec3d(0, 7.5, -4), 0.5f), matLight),
      SceneNode(new Sphere(Vec3d(0, 7.5, 0), 0.5f), matLight),
      SceneNode(new Sphere(Vec3d(0, 7.5, 4), 0.5f), matLight),
      SceneNode(new Sphere(Vec3d(4, 7.5, -4), 0.5f), matLight),
      SceneNode(new Sphere(Vec3d(4, 7.5, 0), 0.5f), matLight),
      SceneNode(new Sphere(Vec3d(4, 7.5, 4), 0.5f), matLight),

      BoundlessNode(new Plane(Vec3d.Up, 0), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Down, 8), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Right, 8), matBlueDiffuse),
      BoundlessNode(new Plane(Vec3d.Left, 8), matRedDiffuse),
      BoundlessNode(new Plane(Vec3d.Front, 8), matGreenDiffuse),
      BoundlessNode(new Plane(Vec3d.Back, 16), matWhiteDiffuse)))

    val window = new JfxDisplay(1280, 720)

    RenderEngine.render(
      target = window,
      tracer = new BidirectionalPathTracer(
        maxCameraBounces = 6,
        maxLightBounces = 2),
      view = new Viewpoint(
        position = Vec3d(0, 2.5, 13),
        forward = Vec3d.Back,
        up = Vec3d.Up),
      scene = new Scene(
        root = coloredSpheres,
        camera = new LensCamera(
            lensRadius = mm(7),
            focussedDepth = 8)))
  }
}