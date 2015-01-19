package kaloffl.spath

import java.util.Arrays
import java.util.function.Consumer
import kaloffl.spath.scene.Scene
import kaloffl.spath.tracing.TracingWorker
import java.util.concurrent.ThreadLocalRandom

class PathTracer {

  val numberOfWorkers = Runtime.getRuntime.availableProcessors * 2
  val rows = Math.sqrt(numberOfWorkers).toInt
  val cols = numberOfWorkers / rows

  println("worker threads: " + numberOfWorkers)

  def render(display: Display, passes: Int, scene: Scene) {
    val tracingWorkers = new Array[TracingWorker](numberOfWorkers)
    val width = display.width / cols
    val height = display.height / rows

    val random = () ⇒ {
      ThreadLocalRandom.current().nextFloat
    }

    for (i ← 0 until numberOfWorkers) {
      val x = i % cols * width
      val y = i / cols * height
      tracingWorkers(i) = new TracingWorker(x, y, width, height, scene, random)
    }

    for (pass ← 0 until passes) {
      println("Starting pass #" + pass)

      val before = System.nanoTime
      Arrays.stream(tracingWorkers).parallel.forEach(new Consumer[TracingWorker] {
        override def accept(worker: TracingWorker): Unit = {
          worker.render(8, display.width, display.height)
          worker.draw(display)
        }
      })
      val after = System.nanoTime
      val duration = after - before
      if (duration > 1000000000) {
        println("rendertime: " + Math.floor(duration / 10000000.0) / 100.0 + "s")
      } else {
        println("rendertime: " + Math.floor(duration / 10000.0) / 100.0 + "ms")
      }

      display.redraw
    }
  }
}