package kaloffl.spath.examples

import kaloffl.spath.Display
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Attenuation
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

object Scatter3 {

  def main(args: Array[String]): Unit = {

    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))

    val matWhiteGlass8 = RefractiveMaterial(Color.White, 1.8f, 0.0)
    val matAir = new TransparentMaterial(
      color = Color(0.02f, 0.01f, 0.005f),
      scatterProbability = 0.005)
    val matLight = new LightMaterial(Color.White * 8, Attenuation.none)

    val hazeObjects = Array(
      SceneNode(new Sphere(Vec3d(-5.0f, 2.0f, 2.5f), 2.0f), matWhiteGlass8),
      SceneNode(new Sphere(Vec3d(-2.5f, 2.0f, -5.0f), 2.0f), matBlackDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 2.0f, 0.0f), 2.0f), matBlackDiffuse),
      SceneNode(new Sphere(Vec3d(2.5f, 1.0f, 6.0f), 1.0f), matBlackDiffuse),
      SceneNode(new Sphere(Vec3d(5.0f, 1.0f, 5.0f), 1.0f), matBlackDiffuse),

      SceneNode(AABB(Vec3d(0, -0.5, 4), Vec3d(16, 1, 24)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(0, 8.5, 4), Vec3d(16, 1, 24)), matLight),
      SceneNode(AABB(Vec3d(8.5f, 4, 4), Vec3d(1, 8, 24)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(-8.5f, 4, 4), Vec3d(1, 8, 24)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(0, 4, -8.5f), Vec3d(16, 8, 1)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(0, 4, 16.5), Vec3d(16, 8, 1)), matBlackDiffuse))

    val blocks = (for (x ← -8 to 8; y ← -8 to 16) yield {
      if (Math.random < 0.8) {
        SceneNode(
          AABB(Vec3d(x, 7.5, y), Vec3d(1, 1, 1)),
          matBlackDiffuse)
      } else {
        null
      }
    }).filter(_ != null).toArray

    val front = Vec3d(0, -2.5, -13).normalize
    val up = front.cross(Vec3d.Right).normalize

    RenderEngine.render(
      bounces = 12,
      target = new Display(1280, 720),
      tracer = new PathTracer(new Scene(
        root = SceneNode(hazeObjects ++ blocks),
        airMedium = matAir,
        camera = new Camera(
          position = Vec3d(0, 5, 13),
          forward = front,
          up = up,
          aperture = 0.1,
          focalLength = Vec3d(0, 2.5, 13).length))))
  }
}