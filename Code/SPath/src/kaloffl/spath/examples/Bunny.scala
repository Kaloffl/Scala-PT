package kaloffl.spath.examples

import kaloffl.spath.Display
import kaloffl.spath.RenderEngine
import kaloffl.spath.importer.PlyImporter
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.materials.UniformSky
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer

object Bunny {

  def main(args: Array[String]): Unit = {

    val matAir = new TransparentMaterial(Color.Black)
    val matGlass = new TransparentMaterial(
      color = Color(0.7f, 1.4f, 1.8f),
      scatterProbability = 4,
      refractiveIndex = 1.7f)
    val matFloor = DiffuseMaterial(Color(0.6f, 0.65f, 0.7f))
    val matBunny = DiffuseMaterial(Color(0.8f, 0.4f, 0.2f))

    val bunny = SceneNode(
      PlyImporter.load("D:/temp/bunny_flipped.ply", Vec3d(40), Vec3d(0, -0.659748 * 2, 0)),
      matBunny)

    val floor = SceneNode(AABB(Vec3d(-1, -0.05, -1), Vec3d(8, 0.1, 8)), matFloor)

    val bunnyForward = Vec3d(-2, -1.75, -10)
    val bunnyTop = bunnyForward.cross(Vec3d.Right).normalize

    RenderEngine.render(
      bounces = 12,
      target = new Display(1280, 720),
      tracer = new PathTracer(new Scene(
        root = SceneNode(Array(floor, bunny)),
        airMedium = matAir,
        skyMaterial = new UniformSky(Color(1.0f, 0.95f, 0.9f) * 2),
        camera = new Camera(
          position = Vec3d(0, 4.5, 9),
          forward = bunnyForward.normalize,
          up = bunnyTop,
          aperture = 0.2f,
          focalLength = bunnyForward.length.toFloat))))
  }
}