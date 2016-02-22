package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.LensCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.CheckeredMask
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.MaskedMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Mandelbulb
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer

object EstimatedShapes {

  def main(args: Array[String]): Unit = {
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = new LightMaterial(Color.White)
    val matRedLight = new LightMaterial(Color.Red * 2)
    val matGreenLight = new LightMaterial(Color.Green * 2)

    val checkeredMask = new CheckeredMask(2, Vec3d(0.5))
    val matBlackWhiteCheckered = new MaskedMaterial(matBlackDiffuse, matWhiteDiffuse, checkeredMask)

    val matAir = new TransparentMaterial(Color.Black, 0, 1.0f)

    val glassTest = SceneNode(Array(
      SceneNode(new Mandelbulb(Vec3d.Origin, 8), matWhiteDiffuse),

      SceneNode(new Sphere(Vec3d(4, 0, 0), 1), matRedLight),
      SceneNode(new Sphere(Vec3d(-4, 0, 0), 1), matGreenLight),

      SceneNode(AABB(Vec3d(0, -1.5, 0), Vec3d(10, 1, 10)), matBlackWhiteCheckered),
      SceneNode(AABB(Vec3d(0, 7.5, 0), Vec3d(10, 1, 10)), matWhiteLight),
      SceneNode(AABB(Vec3d(5.5, 3, 0), Vec3d(1, 8, 10)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(0, 3, -5.5), Vec3d(10, 8, 1)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(-5.5, 3, 0), Vec3d(1, 8, 10)), matWhiteDiffuse),
      SceneNode(AABB(Vec3d(0, 3, 5.5), Vec3d(10, 8, 1)), matWhiteDiffuse)))

    val front = Vec3d(0, -1.5, -2.5).normalize
    val up = Vec3d.Left.cross(front)

    RenderEngine.render(
      bounces = 4,
      target = new JfxDisplay(1280, 720),
      tracer = new PathTracer(new Scene(
        airMedium = matAir,
        root = glassTest,
        camera = new LensCamera(
          position = Vec3d(0, 1.5, 2.5),
          forward = front,
          up = up,
          lensRadius = 0.015f,
          focussedDepth = 3))))
  }
}