package kaloffl.spath

import java.util.Arrays
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kaloffl.spath.scene.Scene
import kaloffl.spath.tracing.TracingWorker
import java.util.function.DoubleSupplier
import kaloffl.jobs.JobPool
import kaloffl.jobs.Job

/**
 * The Engine that can render an image of a given scene. This object handles
 * the distribution of work on multiple workers that can do the rendering in
 * parallel.
 */
object RenderEngine {

  // The squaring of workers here is arbitrary. There probably is a better way 
  // of finding the number of workers. However you definitely want more workers 
  // than cores on the machine (except on single-core machines) because the 
  // chunks of work can take very different amounts of time to render, so if 
  // there is only one chunk per core, all but one workers might finish and then
  // wait on the last one. By having more chunks, workers that finish faster can
  // work on more chunks than the slow workers.
  val processors = Runtime.getRuntime.availableProcessors
  val numberOfWorkers = if (1 == processors) 1 else  processors * processors * 4
  val rows = Math.sqrt(numberOfWorkers).toInt
  val cols = numberOfWorkers / rows

  println("worker threads: " + numberOfWorkers)

  /**
   * Renders the scene with the given number of passes onto the display. In each
   * pass, Rays are shot through each pixel of the image and tested for
   * collision with the world. Once collided, a ray bounces off the surface
   * and is tested again until it hits a light source. Then the color for the
   * pixel is determined from the color of the light and the surfaces it bounced
   * off of.
   *
   * @param target Target to render the resulting pixels onto
   * @param scene The objects and camera for the rendering
   * @param passes Number of rays that are simulated per pixel (default 3000)
   * @param bounces Maximal number of bounces that are simulated per pixel per pass (default 8)
   */
  def render(target: RenderTarget, scene: Scene, passes: Int = 3000, bounces: Int = 8) {
    val tracingWorkers = new Array[TracingWorker](numberOfWorkers)
    val width = target.width / cols
    val height = target.height / rows

    val random = new DoubleSupplier {
      override def getAsDouble = ThreadLocalRandom.current.nextDouble
    }

    for (i ← 0 until numberOfWorkers) {
      val x = i % cols * width
      val y = i / cols * height
      val w = if (x + 1 == cols) target.width - x else width
      val h = if (y + 1 == rows) target.height - y else height
      tracingWorkers(i) = new TracingWorker(x, y, w, h, scene, random)
    }

    var pass = 0
    val pool = new JobPool
    while (pass < passes && tracingWorkers.exists(!_.done)) {
      println("Starting pass #" + pass)

      val before = System.nanoTime
      tracingWorkers.foreach { worker ⇒
        if (!worker.done) {
          pool.submit(new Job {
            def canExecute = true
            def execute = {
              worker.render(bounces, pass, target)
              worker.draw(target)
            }
          })
        }
      }
      pool.execute

      val after = System.nanoTime
      val duration = after - before
      if (duration > 1000000000) {
        println("rendertime: " + Math.floor(duration / 10000000.0) / 100.0 + "s")
      } else {
        println("rendertime: " + Math.floor(duration / 10000.0) / 100.0 + "ms")
      }

      // After each rendering pass, the result is displayed to the user so he 
      // can see the progress.
      target.update
      pass += 1;
    }
  }
}