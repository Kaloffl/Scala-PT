package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.importer.PlyImporter
import kaloffl.spath.math.Color
import kaloffl.spath.math.Quaternion
import kaloffl.spath.math.Transformation
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.PinholeCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.BoundedTransformationNode
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer

object Transformed {

  def main(args: Array[String]): Unit = {
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matLight = new LightMaterial(Color(0.8f, 0.9f, 2f))

    val bunny = SceneNode(
      PlyImporter.load(file = "C:/dev/bunny_flipped.ply", scale = Vec3d(10)),
      matWhiteDiffuse)

    val transformed = SceneNode(Array(
      SceneNode(
        new Sphere(Vec3d(1, 3, 1), 1),
        matLight),

      new BoundedTransformationNode(
        new Transformation(
          scale = Vec3d(4),
          translation = Vec3d(2, 4, -2),
          rotation = Quaternion(Vec3d.Left, Math.PI * 3 / 4)),
        bunny)))

    val focus = Vec3d(0, 1, 0)
    val position = Vec3d(7, 1, 7)
    val forward = (focus - position).normalize
    val up = Vec3d(0, 1, 0)

    RenderEngine.render(
      bounces = 12,
      target = new JfxDisplay(1280, 720),
      tracer = PathTracer,
      scene = new Scene(
        root = transformed,
        camera = new PinholeCamera(
          position = position,
          forward = forward,
          up = up)))
  }
}