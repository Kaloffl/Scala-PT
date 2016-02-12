package kaloffl.spath.examples

import kaloffl.spath.Display
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.hints.GlobalHint
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.ReflectiveMaterial
import kaloffl.spath.scene.materials.RefractiveMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.materials.UniformSky
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Plane
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.BoundlessNode
import kaloffl.spath.scene.structure.ClippingNode
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.RecursivePathTracer

object Scatter5 {
  def main(args: Array[String]): Unit = {

    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))

    val matWhiteLight = new LightMaterial(Color.White * 16)

    val matRedGlass = new TransparentMaterial(
      color = Color(0.1f, 0.5f, 0.5f),
      scatterProbability = 0.1,
      refractiveIndex = 1.7f)
    val matClearGlass = RefractiveMaterial(Color.White, 1.7f)
    val matWhiteGlass = new TransparentMaterial(
      color = Color.Black,
      refractiveIndex = 1.7f)

    val matMirror = ReflectiveMaterial(Color.White, 0.0001f)

    val matAir = new TransparentMaterial(Color.Black)

    val lightShape = new Sphere(Vec3d(0, 80, 0), 20)

    val glassTest = BoundlessNode(Array(
      SceneNode(lightShape, matWhiteLight),
      SceneNode(new Sphere(Vec3d(-20, 10, -10), 10), matMirror),
      SceneNode(new Sphere(Vec3d(-10, 5, 20), 5), matWhiteGlass),

      new ClippingNode(
        SceneNode(Array(
          new Sphere(Vec3d(15, 10, 0), 10),
          new Sphere(Vec3d(15, 25, 0), 5),
          new Sphere(Vec3d(15, 32.5, 0), 2.5f),
          new Sphere(Vec3d(15, 36.25, 0), 1.25f),
          new Sphere(Vec3d(15, 38.125, 0), 0.625f)),
          matRedGlass),
        new AABB(Vec3d(5, 0, -10), Vec3d(25, 38.75, 10))),

      SceneNode(Array(
        new Sphere(Vec3d(1, 2.5, -6), 2.5f),
        new Sphere(Vec3d(-10, 2.5, -4), 2.5f),
        new Sphere(Vec3d(0, 1.25, 0), 1.25f),
        new Sphere(Vec3d(-4, 1.25, 3), 1.25f)),
        matClearGlass),

      BoundlessNode(new Plane(Vec3d.Up, 0), matWhiteDiffuse)))

    val front = Vec3d(0, -1, -1).normalize
    val up = Vec3d.Left.cross(front)

    RenderEngine.render(
      passes = 6000,
      bounces = 8,
      target = new Display(700, 700),
      tracer = new RecursivePathTracer(new Scene(
        root = glassTest,
        initialMediaStack = Array(matAir),
        lightHints = Array(GlobalHint(lightShape)),
        skyMaterial = new UniformSky(Color(0.9f, 0.95f, 0.975f) * 0.5f),
        camera = new Camera(
          position = Vec3d(0, 60, 60),
          forward = front,
          up = up))))
  }
}