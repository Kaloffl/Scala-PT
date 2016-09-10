package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.RenderTarget
import kaloffl.spath.scene.{Scene, Viewpoint}

/**
 * A job that renders a chunk of the final image by shooting rays through
 * the pixels it works on.
 *
 * @param left the x position of the left side of the area this worker renders
 * @param top the y position of the top side of the area this worker renders
 * @param width the width of the area this worker renders
 * @param height the height of the area this worker renders
 *
 * @param tracer the tracer instance used to render samples
 * @param scene the scene that is rendered
 * @param target the target that will receive the rendered pixels
 * @param random a provider of random values
 */
class TracingJob(
                  val left: Int,
                  val top: Int,
                  val width: Int,
                  val height: Int,
                  val tracer: Tracer,
                  val scene: Scene,
                  val target: RenderTarget,
                  val random: DoubleSupplier) {

  val sampleStorage = new SampleStorage(width, height)

  /**
   * Renders a given number of passes for each pixel in the workers section and stores the results
   */
  def render(view: Viewpoint, passes: Int = 1, cpuSaturation: Float = 1): Unit = {

    // get the full size of the image from the target
    val fullWidth = target.width
    val fullHeight = target.height

    // add some random offset for anti-aliasing
    val displayOffsetX = left + (random.getAsDouble - 0.5).toFloat
    val displayOffsetY = top + (random.getAsDouble - 0.5).toFloat

    // accumulator for the time the program needs to sleep to meet the required cpu saturation
    var wait = 0L

    for (x <- 0 until width; y <- 0 until height) {
      val rx = (2 * (x + displayOffsetX) - fullWidth) / fullWidth
      val ry = (fullHeight - 2 * (y + displayOffsetY)) / fullWidth
      val ray = scene.camera.createRay(view, random, rx, ry)
      for (_ <- 0 until passes) {
        val start = System.nanoTime

        val color = tracer.trace(
          ray = ray,
          scene = scene,
          random = random)

        sampleStorage.addSample(x, y, color)
        
        if (cpuSaturation < 1) {
          val stop = System.nanoTime
          wait += ((stop - start) * (1 / cpuSaturation - 1)).toLong
          // waiting is handled in chunks of at least 3ms to reduce the number sleep calls
          if (wait >= 3000000) {
            val ms = wait / 1000000
            wait -= ms * 1000000
            // to prevent too long sleeps, they are limited to 2s
            Thread.sleep(ms % 2000)
          }
        }
      }
    }
  }

  /**
   * Submits the current samples to the target
   */
  def draw(): Unit = {
    for (x <- 0 until width; y <- 0 until height) {
      val color = sampleStorage.getColor(x, y)
      target.setPixel(x + left, y + top, color)
    }
  }
}
