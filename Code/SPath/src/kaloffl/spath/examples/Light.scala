package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.RefractiveMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer

object Light {

  def main(args: Array[String]): Unit = {

    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteGlass8 = RefractiveMaterial(Color.White, 1.8f)

    val matAir = new TransparentMaterial(
      color = Color(0.02f, 0.01f, 0.005f),
      scatterProbability = 0.02)

    val coloredLights = SceneNode(Array(
      SceneNode(new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f), matWhiteGlass8),
      SceneNode(new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f), matWhiteDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f), matWhiteDiffuse),

      SceneNode(AABB(Vec3d(-4, 7.5, 4), Vec3d(3, 0.125, 22)),
        new LightMaterial(Color(0.9f, 0.1f, 0.1f) * 2)),
      SceneNode(AABB(Vec3d(0, 7.5, 4), Vec3d(3, 0.125, 22)),
        new LightMaterial(Color(0.1f, 0.9f, 0.1f) * 2)),
      SceneNode(AABB(Vec3d(4, 7.5, 4), Vec3d(3, 0.125, 22)),
        new LightMaterial(Color(0.1f, 0.1f, 0.9f) * 2)),

      SceneNode(AABB(Vec3d(-7.5, 7, 4), Vec3d(1, 2, 22)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(-2.5, 7, 4), Vec3d(2, 2, 22)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(2.5, 7, 4), Vec3d(2, 2, 22)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(7.5, 7, 4), Vec3d(1, 2, 22)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(0, 7, -8.5), Vec3d(16, 2, 1)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(0, 7, 16.5), Vec3d(16, 2, 1)), matBlackDiffuse),

      SceneNode(AABB(Vec3d(0, -0.5, 4), Vec3d(16, 1, 24)), matWhiteDiffuse),
      //SceneNode(AABB(Vec3d(0, 8.5, 4), Vec3d(16, 1, 24)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(8.5f, 4, 4), Vec3d(1, 8, 24)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(-8.5f, 4, 4), Vec3d(1, 8, 24)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(0, 4, -8.5f), Vec3d(16, 8, 1)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(0, 4, 16.5), Vec3d(16, 8, 1)), matWhiteDiffuse)))

    val front = Vec3d(0, -2.5, -13)
    val up = front.normalize.cross(Vec3d.Right).normalize

    RenderEngine.render(
      bounces = 12,
      target = new JfxDisplay(1280, 720),
      tracer = new PathTracer(new Scene(
        root = coloredLights,
        airMedium = matAir,
        camera = new Camera(
          position = Vec3d(0, 5, 13),
          forward = front.normalize,
          up = up,
          aperture = 0.1f,
          focalLength = front.length.toFloat))))
  }
}