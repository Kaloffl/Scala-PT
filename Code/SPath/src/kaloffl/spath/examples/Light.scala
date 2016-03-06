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

object Light {

  def main(args: Array[String]): Unit = {

    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteGlass8 = RefractiveMaterial(Color.White, 1.8f)

    val coloredLights = BoundlessNode(Array(
      SceneNode(new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f), matWhiteGlass8),
      SceneNode(new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f), matWhiteDiffuse),

      SceneNode(
        AABB(Vec3d(-4, 7.5, 4), Vec3d(3, 0.125, 22)),
        LightMaterial(Color(0.9f, 0.1f, 0.1f) * 2)),
      SceneNode(
        AABB(Vec3d(0, 7.5, 4), Vec3d(3, 0.125, 22)),
        LightMaterial(Color(0.1f, 0.9f, 0.1f) * 2)),
      SceneNode(
        AABB(Vec3d(4, 7.5, 4), Vec3d(3, 0.125, 22)),
        LightMaterial(Color(0.1f, 0.1f, 0.9f) * 2)),

      BoundlessNode(new Plane(Vec3d.Left, 8), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Right, 8), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Up, 0), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Down, 8), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Front, 8), matWhiteDiffuse),
      BoundlessNode(new Plane(Vec3d.Back, 16), matWhiteDiffuse)))

    val front = Vec3d(0, -2.5, -13)
    val up = front.normalize.cross(Vec3d.Right).normalize

    RenderEngine.render(
      bounces = 12,
      target = new JfxDisplay(1280, 720),
      tracer = PathTracer,
      view = new Viewpoint(
        position = Vec3d(0, 5, 13),
        forward = front.normalize,
        up = up),
      scene = new Scene(
        root = coloredLights,
        camera = new PinholeCamera))
  }
}