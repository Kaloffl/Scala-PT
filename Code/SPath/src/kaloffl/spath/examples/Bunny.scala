package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.importer.PlyImporter
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.LensCamera
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
      PlyImporter.load("C:/dev/bunny_flipped.ply", Vec3d(4), Vec3d(0, -0.659748 / 5, 0)),
      matBunny)

    val floor = SceneNode(AABB(Vec3d(-0.1, -0.005, -0.1), Vec3d(0.8, 0.01, 0.8)), matFloor)

    val bunnyForward = Vec3d(-0.2, -0.175, -1)
    val bunnyTop = bunnyForward.cross(Vec3d.Right).normalize

    RenderEngine.render(
      bounces = 12,
      target = new JfxDisplay(1280, 720),
      tracer = new PathTracer(new Scene(
        root = SceneNode(Array(floor, bunny)),
        airMedium = matAir,
        skyMaterial = new UniformSky(Color(1.0f, 0.95f, 0.9f) * 2),
        camera = new LensCamera(
          position = Vec3d(0, 0.45, 0.9),
          forward = bunnyForward.normalize,
          up = bunnyTop,
          lensRadius = 0.02f,
          focussedDepth = bunnyForward.length.toFloat))))
  }
}