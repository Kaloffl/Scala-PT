package kaloffl.spath.examples

import kaloffl.spath.importer.PlyImporter
import kaloffl.spath.math.Units.{cm, mm}
import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.scene.materials.{DiffuseMaterial, TransparentMaterial, UniformSky}
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.{PinholeCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.{PathTracer, RecursivePathTracer}
import kaloffl.spath.{JfxDisplay, RenderEngine, RtApplication}

object Bunny {

  def main(args: Array[String]): Unit = {

    val matGlass = new TransparentMaterial(
      //volumeColor = Color(0.4f, 0.7f, 1.0f),
      //absorbtionDepth = mm(1),
      //scatterProbability = 4,
      ior = 2.4f)
    val matFloor = DiffuseMaterial(Color(0.6f, 0.65f, 0.7f))
    val matBunny = DiffuseMaterial(Color(0.8f, 0.4f, 0.2f))

    val bunny = SceneNode(
      PlyImporter.load(
        file = "C:/dev/bunny_flipped.ply",
        scale = Vec3d(3),
        offset = Vec3d(0, -0.1319496 / 2, 0)),
      matGlass)

    val floor = SceneNode(AABB(Vec3d(0, mm(-1), 0), Vec3d(cm(5), mm(2), cm(5))), matFloor)

    val camPosition = Vec3d(mm(0), cm(3), cm(5))
    val focusPoint = Vec3d(cm(-1), cm(2), cm(1))
    val distance = focusPoint - camPosition
    val forward = distance.normalize
    val right = Vec3d.Up.cross(forward).normalize
    val up = forward.cross(right).normalize

    val window = new JfxDisplay(1280, 720)

    RenderEngine.render(
      target = window,
      tracer = new RecursivePathTracer(maxBounces = 12),
      cpuSaturation = 0.5f,
      view = new Viewpoint(
        position = camPosition,
        forward = forward,
        up = up),
      scene = new Scene(
        root = SceneNode(Array(floor, bunny)),
        skyMaterial = new UniformSky(Color(1.0f, 0.95f, 0.9f) * 2),
        camera = new PinholeCamera))
  }
}