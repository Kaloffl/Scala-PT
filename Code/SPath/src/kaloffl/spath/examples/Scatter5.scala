package kaloffl.spath.examples

import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.sampler.SphereSampler
import kaloffl.spath.scene.materials._
import kaloffl.spath.scene.shapes.{AABB, Plane, Sphere}
import kaloffl.spath.scene.structure.{BoundlessNode, ClippingNode, SceneNode}
import kaloffl.spath.scene.{GlobalHint, PinholeCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.RecursivePathTracer
import kaloffl.spath.{JfxDisplay, RenderEngine}

object Scatter5 {
  def main(args: Array[String]): Unit = {

    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = new EmittingMaterial(Color.White, 16)

    val matRedGlass = new TransparentMaterial(
      volumeColor = Color(0.1f, 0.5f, 0.5f),
      scatterProbability = 0.1f,
      ior  = 1.7f)
    val matClearGlass = new TransparentMaterial(ior = 1.7f)

    val matMirror = new MetalMaterial((_, _) => Color.White * 0.0001f)

    val lightShape = new Sphere(Vec3d(0, 80, 0), 20)

    val glassTest = BoundlessNode(Array(
      SceneNode(lightShape, matWhiteLight),
      SceneNode(new Sphere(Vec3d(-20, 10, -10), 10), matMirror),
      SceneNode(new Sphere(Vec3d(-10, 5, 20), 5), matClearGlass),

      new ClippingNode(
        SceneNode(Array(
          new Sphere(Vec3d(15, 10, 0), 10),
          new Sphere(Vec3d(15, 25, 0), 5),
          new Sphere(Vec3d(15, 32.5, 0), 2.5f),
          new Sphere(Vec3d(15, 36.25, 0), 1.25f),
          new Sphere(Vec3d(15, 38.125, 0), 0.625f)),
          matRedGlass),
        new AABB(Vec3d(5, 0, -10), Vec3d(25, 38.75, 10))),

      SceneNode(Array(
        new Sphere(Vec3d(1, 2.5, -6), 2.5f),
        new Sphere(Vec3d(-10, 2.5, -4), 2.5f),
        new Sphere(Vec3d(0, 1.25, 0), 1.25f),
        new Sphere(Vec3d(-4, 1.25, 3), 1.25f)),
        matClearGlass),

      BoundlessNode(new Plane(Vec3d.Up, 0), matWhiteDiffuse)))

    val front = Vec3d(0, -1, -1).normalize
    val up = Vec3d.Left.cross(front)

    RenderEngine.render(
      target = new JfxDisplay(700, 700),
      tracer = new RecursivePathTracer(maxBounces = 8),
      view = new Viewpoint(
        position = Vec3d(0, 60, 60),
        forward = front,
        up = up),
      scene = new Scene(
        root = glassTest,
        lightHints = Array(GlobalHint(new SphereSampler(lightShape))),
        skyMaterial = new UniformSky(Color(0.9f, 0.95f, 0.975f) * 0.5f),
        camera = new PinholeCamera))
  }
}