package kaloffl.spath

import java.util.concurrent.ThreadLocalRandom
import java.util.function.DoubleSupplier

import kaloffl.jobs.{Job, JobPool}
import kaloffl.spath.scene.{Scene, Viewpoint}
import kaloffl.spath.tracing._

import scala.util.Sorting

/**
 * The Engine that can render a still image of a given scene. This object handles
 * the distribution of work on multiple workers that can do the rendering in
 * parallel.
 */
object RenderEngine {

  val processors = Runtime.getRuntime.availableProcessors
  // The squaring of jobs here is arbitrary. There probably is a better way
  // of finding the number of jobs. However you definitely want more jobs
  // than cores on the machine (except on single-core machines) because the
  // chunks of work can take very different amounts of time to render, so if
  // there is only one chunk per core, the whole programm has to wait for the
  // slowest one to finish.
  val numberOfJobs = if (1 == processors) 1 else processors * processors * 4
  val rows = Math.sqrt(numberOfJobs).toInt
  val cols = numberOfJobs / rows

  /**
   * Renders the scene with the given number of passes onto the display. In each
   * pass, Rays are shot through each pixel of the image and tested for
   * collision with the world. Once collided, a ray bounces off the surface
   * and is tested again until it hits a light source. Then the color for the
   * pixel is determined from the color of the light and the surfaces it bounced
   * off of.
   *
   * @param target Target to render the resulting pixels onto
   * @param tracer The tracer implementation to use for rendering
   * @param logger optional logger for status reports
   * @param scene The objects and camera for the rendering
   * @param view The location and orientation from where to render
   * @param samplesAtOnce the number of samples that should be taken for every
   *                      pixel before updating the target. Higher values are
   *                      more efficient but take longer to show up in the
   *                      preview (default 1)
   * @param cpuSaturation on a scale from 0 (none) to 1 (all) how much cpu
   *                      utilization the rendering should aim for (default 1)
   */
  def render(
              target: RenderTarget,
              tracer: Tracer,
              logger: String => Unit = print(_),
              scene: Scene,
              view: Viewpoint,
              samplesAtOnce: Int = 1,
              cpuSaturation: Float = 1
            ): Unit = {

    logger("number of chunks: " + numberOfJobs + '\n')
    logger("target cpu saturation: " + cpuSaturation + '\n')

    val tracingJobs = new Array[TracingJob](numberOfJobs)
    val width = target.width / cols
    val height = target.height / rows

    val random = new DoubleSupplier {
      override def getAsDouble = ThreadLocalRandom.current.nextDouble
    }

    for (i <- 0 until numberOfJobs) {
      val x = i % cols * width
      val y = i / cols * height
      val w = if ((i + 1) % cols == 0) target.width - x else width
      val h = if (i >= cols * (rows - 1)) target.height - y else height
      tracingJobs(i) = new TracingJob(x, y, w, h, tracer, scene, target, random)
    }

    var pass = 0
    val pool = new JobPool
    val order = Array.tabulate(numberOfJobs)(identity) // fills the array cells with their own index
    val costs = new Array[Long](numberOfJobs)
    while (true) {
      if (1 == samplesAtOnce) {
        logger("Starting pass #" + pass + '\n')
      } else {
        logger("Starting passes #" + pass + " - #" + (pass + samplesAtOnce - 1) + '\n')
      }

      val before = System.nanoTime
      for (i <- 0 until numberOfJobs) {
        val job = tracingJobs(order(i))
        pool.submit(new Job {
          override def execute(): Unit = {
            val start = System.nanoTime
            job.render(view, samplesAtOnce, cpuSaturation)
            job.draw()
            costs(order(i)) += System.nanoTime - start
          }
        })
      }
      pool.execute()

      // A small optimization: we sort the parts that will take the longest to
      // the front so that they will be done first. That way we have the small
      // parts left to easily fill time for threads that are done early.
      // This way we get the best saturation for all working threads.
      if (pass == 10) {
        // On the tenth pass the numbers should have stabilized enough to 
        // determine which parts are the most expensive to render.
        Sorting.stableSort(order, (o1: Int, o2: Int) => costs(order(o1)) > costs(order(o2)))
      }

      val after = System.nanoTime
      val duration = after - before
      if (duration > 1000000000) {
        logger("rendertime: " + Math.floor(duration / 10000000.0) / 100.0 + "s")
      } else {
        logger("rendertime: " + Math.floor(duration / 10000.0) / 100.0 + "ms")
      }
      if (samplesAtOnce > 1) {
        val singleDur = duration / samplesAtOnce
        if (singleDur > 1000000000) {
          logger(" (~" + Math.floor(singleDur / 10000000.0) / 100.0 + "s/sample)")
        } else {
          logger(" (~" + Math.floor(singleDur / 10000.0) / 100.0 + "ms/sample)")
        }
      }
      logger("\n")

      // After each rendering pass, the workers will have written their results
      // into the target and all that's left is calling commit to signal that 
      // the pass is done.
      target.commit()
      pass += samplesAtOnce
    }
  }
}