package kaloffl.spath.examples

import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.sampler.{SphereSampler, TriangleSampler}
import kaloffl.spath.scene.materials.{DiffuseMaterial, EmittingMaterial}
import kaloffl.spath.scene.shapes.{AABB, Plane, Sphere, Triangle}
import kaloffl.spath.scene.structure.{BoundlessNode, SceneNode}
import kaloffl.spath.scene.{GlobalHint, PinholeCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.RecursivePathTracer
import kaloffl.spath.{JfxDisplay, RenderEngine}

object LightHints {

  def main(args: Array[String]): Unit = {

    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matLight = new EmittingMaterial(Color.White, 100000)

    val tri1 = new Triangle(Vec3d(5.1, 7, 0), Vec3d(4.9, 7, 0.1), Vec3d(4.9, 7, -0.1))
    val tri2 = new Triangle(Vec3d(-5.1, 7, 0), Vec3d(-4.9, 7, 0.1), Vec3d(-4.9, 7, -0.1))
    val sphere = new Sphere(Vec3d(-5, 7, 0), 0.2f)

    val objects = BoundlessNode(Array(
      SceneNode(
        AABB(Vec3d(0, 5, 0), Vec3d(0.5, 10, 10)),
        matWhiteDiffuse),
      SceneNode(tri1, matLight),
      SceneNode(tri2, matLight),
      BoundlessNode(new Plane(Vec3d.Up, 0), matWhiteDiffuse)))

    val front = Vec3d(0, -2.5, -13)
    val up = front.normalize.cross(Vec3d.Right).normalize

    RenderEngine.render(
      target = new JfxDisplay(1280, 720),
      tracer = new RecursivePathTracer(maxBounces = 4),
      cpuSaturation = 0.25f,
      view = new Viewpoint(
        position = Vec3d(0, 5, 13),
        forward = front.normalize,
        up = up),
      scene = new Scene(
        root = objects,
        lightHints = Array(
          GlobalHint(new TriangleSampler(tri1)),
          GlobalHint(new SphereSampler(sphere))),
        camera = new PinholeCamera))
  }
}