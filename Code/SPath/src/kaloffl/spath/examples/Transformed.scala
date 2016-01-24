package kaloffl.spath.examples

import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Attenuation
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.materials.ReflectiveMaterial
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.math.Color
import kaloffl.spath.scene.Scene
import kaloffl.spath.tracing.PathTracer
import kaloffl.spath.Display
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.structure.TransformationNode
import kaloffl.spath.math.Transformation
import kaloffl.spath.tracing.NormalTracer
import kaloffl.spath.math.Quaternion
import kaloffl.spath.importer.PlyImporter

object Transformed {

  def main(args: Array[String]): Unit = {
    val matRedDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matAir = new TransparentMaterial(Color.Black)

    val matMirror = ReflectiveMaterial(Color.White, 0.0)
    val matGlass = new TransparentMaterial(
      color = Color(0.09f, 0.09f, 0.09f),
      refractiveIndex = 2.0f)
    val matLight = new LightMaterial(Color.White * 8f, Attenuation.radius(1f))

    val box = SceneNode(AABB(Vec3d.Origin, Vec3d(1)), matWhiteDiffuse)
    val scale = Vec3d(1, 2, 1)

    val bunny = SceneNode(
      PlyImporter.load(file = "D:/temp/bunny_flipped.ply", scale = Vec3d(10)),
      matWhiteDiffuse)

    val coloredSpheres = SceneNode(Array(
      new TransformationNode(
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
      target = new Display(1280, 720),
      tracer = new NormalTracer(new Scene(
        root = coloredSpheres,
        airMedium = matAir,
        camera = new Camera(
          position = position,
          forward = forward,
          up = up))))
  }
}