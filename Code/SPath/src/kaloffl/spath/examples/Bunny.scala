package kaloffl.spath.examples

import kaloffl.spath.importer.PlyImporter
import kaloffl.spath.math.Units.{cm, mm}
import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.scene.materials.{DiffuseMaterial, TransparentMaterial, UniformSky}
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.{PinholeCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.PathTracer
import kaloffl.spath.{JfxDisplay, RtApplication}

object Bunny {

  def main(args: Array[String]): Unit = {

    val matGlass = new TransparentMaterial(
      volumeColor = Color(0.4f, 0.7f, 1.0f),
      absorbtionDepth = mm(1),
      scatterProbability = 4,
      ior = 1.7f)
    val matFloor = DiffuseMaterial(Color(0.6f, 0.65f, 0.7f))
    val matBunny = DiffuseMaterial(Color(0.8f, 0.4f, 0.2f))

    val bunny = SceneNode(
      PlyImporter.load(
        file = "C:/dev/bunny_flipped.ply",
        scale = Vec3d(3),
        offset = Vec3d(0, -0.1319496 / 2, 0)),
      matBunny)

    val floor = SceneNode(AABB(Vec3d(0, mm(-1), 0), Vec3d(cm(5), mm(2), cm(5))), matFloor)

    val camPosition = Vec3d(cm(-1), cm(2), cm(5))
    val focusPoint = Vec3d(cm(-2), cm(1.5f), cm(1))
    val distance = focusPoint - camPosition
    val forward = distance.normalize
    val up = Vec3d(0, -forward.z, forward.y).normalize

    val window = new JfxDisplay(1280, 720)
    
    RtApplication.run(
      bounces = 12,
      target = window,
      events = window.events,
      tracer = PathTracer,
      initialView = new Viewpoint(
        position = camPosition,
        forward = forward,
        up = up),
      scene = new Scene(
        root = SceneNode(Array(floor, bunny)),
        skyMaterial = new UniformSky(Color(1.0f, 0.95f, 0.9f) * 2),
        camera = new PinholeCamera))
  }
}