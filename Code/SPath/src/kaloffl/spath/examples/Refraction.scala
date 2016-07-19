package kaloffl.spath.examples

import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.scene.materials.{DiffuseMaterial, EmittingMaterial, TransparentMaterial}
import kaloffl.spath.scene.shapes.{AABB, Sphere}
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.{PinholeCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.PathTracer
import kaloffl.spath.{JfxDisplay, RenderEngine}

object Refraction {

  def main(args: Array[String]): Unit = {

    val matWhiteGlass0 = new TransparentMaterial(ior = 1.0f)
    val matWhiteGlass1 = new TransparentMaterial(ior = 1.1f)
    val matWhiteGlass2 = new TransparentMaterial(ior = 1.2f)
    val matWhiteGlass3 = new TransparentMaterial(ior = 1.3f)
    val matWhiteGlass4 = new TransparentMaterial(ior = 1.4f)
    val matWhiteGlass5 = new TransparentMaterial(ior = 1.5f)
    val matWhiteGlass6 = new TransparentMaterial(ior = 1.6f)
    val matWhiteGlass7 = new TransparentMaterial(ior = 1.7f)
    val matWhiteGlass8 = new TransparentMaterial(ior = 1.8f)
    val matWhiteGlass9 = new TransparentMaterial(ior = 1.9f)

    val matRedDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = new EmittingMaterial(Color.White, 2)

    val glassTest = SceneNode(Array(
      SceneNode(new Sphere(Vec3d(-9, 1, 0), 1), matWhiteGlass0),
      SceneNode(new Sphere(Vec3d(-7, 1, 0), 1), matWhiteGlass1),
      SceneNode(new Sphere(Vec3d(-5, 1, 0), 1), matWhiteGlass2),
      SceneNode(new Sphere(Vec3d(-3, 1, 0), 1), matWhiteGlass3),
      SceneNode(new Sphere(Vec3d(-1, 1, 0), 1), matWhiteGlass4),
      SceneNode(new Sphere(Vec3d(1, 1, 0), 1), matWhiteGlass5),
      SceneNode(new Sphere(Vec3d(3, 1, 0), 1), matWhiteGlass6),
      SceneNode(new Sphere(Vec3d(5, 1, 0), 1), matWhiteGlass7),
      SceneNode(new Sphere(Vec3d(7, 1, 0), 1), matWhiteGlass8),
      SceneNode(new Sphere(Vec3d(9, 1, 0), 1), matWhiteGlass9),

      SceneNode(AABB(Vec3d(-9, 1, 4), Vec3d(1.5)), matWhiteGlass0),
      SceneNode(AABB(Vec3d(-7, 1, 4), Vec3d(1.5)), matWhiteGlass1),
      SceneNode(AABB(Vec3d(-5, 1, 4), Vec3d(1.5)), matWhiteGlass2),
      SceneNode(AABB(Vec3d(-3, 1, 4), Vec3d(1.5)), matWhiteGlass3),
      SceneNode(AABB(Vec3d(-1, 1, 4), Vec3d(1.5)), matWhiteGlass4),
      SceneNode(AABB(Vec3d(1, 1, 4), Vec3d(1.5)), matWhiteGlass5),
      SceneNode(AABB(Vec3d(3, 1, 4), Vec3d(1.5)), matWhiteGlass6),
      SceneNode(AABB(Vec3d(5, 1, 4), Vec3d(1.5)), matWhiteGlass7),
      SceneNode(AABB(Vec3d(7, 1, 4), Vec3d(1.5)), matWhiteGlass8),
      SceneNode(AABB(Vec3d(9, 1, 4), Vec3d(1.5)), matWhiteGlass9),

      SceneNode(AABB(Vec3d(0, 8.5, 4), Vec3d(20, 1, 24)), matWhiteLight),

      SceneNode(AABB(Vec3d(0, -0.5, 4), Vec3d(20, 1, 24)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(10.5f, 4, 4), Vec3d(1, 8, 24)), matRedDiffuse),
      SceneNode(AABB(Vec3d(-10.5f, 4, 4), Vec3d(1, 8, 24)), matBlueDiffuse),
      SceneNode(AABB(Vec3d(0, 4, -8.5f), Vec3d(20, 8, 1)), matGreenDiffuse),
      SceneNode(AABB(Vec3d(0, 4, 16.5), Vec3d(20, 8, 1)), matWhiteDiffuse)))

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
        root = glassTest,
        camera = new PinholeCamera))
  }
}