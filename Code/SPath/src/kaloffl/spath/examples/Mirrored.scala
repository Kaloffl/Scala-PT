package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.LensCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.Viewpoint
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.ReflectiveMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer

object Mirrored {

  def main(args: Array[String]): Unit = {

    val matMirror = ReflectiveMaterial(Color.White)
    val matAir = new TransparentMaterial(
      color = Color(0.02f, 0.01f, 0.005f),
      scatterProbability = 0.002f)

    val coloredSpheres = SceneNode(Array(
      SceneNode(
        new Sphere(Vec3d(0.0, 2.5, 0.0), 2.0f),
        DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))),

      SceneNode(
        AABB(Vec3d(0, -0.5, 0), Vec3d(16, 1, 24)),
        ReflectiveMaterial(Color(0.2f, 0.8f, 0.2f), 0.4f)),
      SceneNode(
        AABB(Vec3d(0, 32.5, 0), Vec3d(16, 1, 24)),
        LightMaterial(Color.White)),
      SceneNode(AABB(Vec3d(8.5f, 16, 0), Vec3d(1, 32, 24)), matMirror),
      SceneNode(AABB(Vec3d(-8.5f, 16, 0), Vec3d(1, 32, 24)), matMirror),
      SceneNode(AABB(Vec3d(0, 16, -12.5f), Vec3d(16, 32, 1)), matMirror),
      SceneNode(AABB(Vec3d(0, 16, 12.5), Vec3d(16, 32, 1)), matMirror)))

    RenderEngine.render(
      bounces = 80,
      target = new JfxDisplay(1280, 720),
      tracer = PathTracer,
      view = new Viewpoint(
        position = Vec3d(0, 2.5, 10),
        forward = Vec3d.Back,
        up = Vec3d.Up),
      scene = new Scene(
        root = coloredSpheres,
        initialMediaStack = Array(matAir),
        camera = new LensCamera(
          lensRadius = 0.03f,
          focussedDepth = 10)))
  }
}