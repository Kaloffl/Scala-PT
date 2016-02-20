package kaloffl.spath.examples

import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.filter.BloomFilter
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.hints.GlobalHint
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.RecursivePathTracer

object LightScatter {

  def main(args: Array[String]): Unit = {

    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matAir = new TransparentMaterial(
      color = Color(0.08f, 0.09f, 0.095f),
      scatterProbability = 0.0125f)
    val matLight = new LightMaterial(Color(1, 0.9f, 0.8f) * 8)

    val lightSphere = new Sphere(Vec3d(0, 0, 0), 1)
    
    val hazeObjects = SceneNode(Array(
      SceneNode(lightSphere, matLight),
      SceneNode(AABB(Vec3d(1.05, 0, 0), Vec3d(0.1, 1.5, 1.5)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(-1.05, 0, 0), Vec3d(0.1, 1.5, 1.5)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(0, 1.05, 0), Vec3d(1.5, 0.1, 1.5)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(0, -1.05, 0), Vec3d(1.5, 0.1, 1.5)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(0, 0, 1.05), Vec3d(1.5, 1.5, 0.1)), matBlackDiffuse),
      SceneNode(AABB(Vec3d(0, 0, -1.05), Vec3d(1.5, 1.5, 0.1)), matBlackDiffuse)))

    RenderEngine.render(
      bounces = 4,
      target = new BloomFilter(new JfxDisplay(1280, 720), 10, 1.5f),
      tracer = new RecursivePathTracer(new Scene(
        root = hazeObjects,
        initialMediaStack = Array(matAir),
        lightHints = Array(GlobalHint(lightSphere)),
        camera = new Camera(
          position = Vec3d(0, 0, 13),
          forward = Vec3d.Back,
          up = Vec3d.Up))))
  }
}