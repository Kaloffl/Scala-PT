package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.PinholeCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.Viewpoint
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.RefractiveMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Plane
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.BoundlessNode
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer
import kaloffl.spath.tracing.RecursivePathTracer
import kaloffl.spath.scene.shapes.Triangle
import kaloffl.spath.scene.hints.GlobalHint

object LightHints {

  def main(args: Array[String]): Unit = {

    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matLight = LightMaterial(Color.White * 100000)

    val tri1 = new Triangle(Vec3d(5.1, 7, 0), Vec3d(4.9, 7, 0.1), Vec3d(4.9, 7, -0.1))
    val tri2 = new Triangle(Vec3d(-5.1, 7, 0), Vec3d(-4.9, 7, 0.1), Vec3d(-4.9, 7, -0.1))
    val sphere = new Sphere(Vec3d(-5, 7, 0), 0.2f)

    val objects = BoundlessNode(Array(
      SceneNode(
        AABB(Vec3d(0, 5, 0), Vec3d(0.5, 10, 10)),
        matWhiteDiffuse),
      SceneNode(tri1, matLight),
      SceneNode(tri2, matLight),
//      SceneNode(sphere, matLight),
      BoundlessNode(new Plane(Vec3d.Up, 0), matWhiteDiffuse)))

    val front = Vec3d(0, -2.5, -13)
    val up = front.normalize.cross(Vec3d.Right).normalize

    RenderEngine.render(
      bounces = 12,
      target = new JfxDisplay(1280, 720),
      tracer = RecursivePathTracer,
      view = new Viewpoint(
        position = Vec3d(0, 5, 13),
        forward = front.normalize,
        up = up),
      scene = new Scene(
        root = objects,
        lightHints = Array(new GlobalHint(tri1), new GlobalHint(sphere)),
        camera = new PinholeCamera))
  }
}