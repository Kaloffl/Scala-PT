package kaloffl.spath.test

import kaloffl.spath.tracing.Ray
import kaloffl.spath.math.Vec3f
import kaloffl.spath.tracing.TracingWorker
import kaloffl.spath.Display
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.Material
import kaloffl.spath.scene.shapes.Plane

object RaySpeedTest {

  def main(args: Array[String]): Unit = {

    val matWhiteLight = new Material(Vec3f.WHITE, Vec3f.WHITE, 0, 0, 0, 0)
    val matWhiteDiffuse = new Material(Vec3f.BLACK, Vec3f.WHITE, 0, 0, 0, 0)

    val camera = new Camera(Vec3f(-5, 1, 0), Vec3f.LEFT, Vec3f.UP, 1, 1)
    val scene = new Scene(Seq(
      new SceneObject(
        new Plane(Vec3f.UP, 0),
        matWhiteLight),
      new SceneObject(
        new Plane(Vec3f.LEFT, 6),
        matWhiteDiffuse),
      new SceneObject(
        new Plane(Vec3f.FRONT, 6),
        matWhiteDiffuse),
      new SceneObject(
        new Sphere(Vec3f(0, 1, 0), 1),
        matWhiteDiffuse),
      new SceneObject(
        new Plane(Vec3f.DOWN, 6),
        matWhiteDiffuse),
      new SceneObject(
        new Plane(Vec3f.RIGHT, 6),
        matWhiteDiffuse),
      new SceneObject(
        new Plane(Vec3f.BACK, 6),
        matWhiteDiffuse)),
      camera)

    val worker = new TracingWorker(0, 0, 1280, 720, scene, () ⇒ 0)

    val ray = camera.createRay(() ⇒ 0, 640, 360, 1280, 720)

    // warmup
    val times = for (i ← 0 until 1000) worker.pathTrace(ray, 8)

    for (max ← Seq(100, 1000, 10000, 100000, 1000000)) {
      var color = Vec3f.BLACK
      val times = for (i ← 0 until max) yield {
        val before = System.nanoTime()
        color = color + worker.pathTrace(ray, 8)
        System.nanoTime() - before
      }

      println(color)
      printf("runs: %12d, min time: %12dns, max time: %12dns, average: %12dns\n",
        max,
        times.min,
        times.max,
        times.sum / times.length)
    }
  }
}