package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.RenderTarget
import kaloffl.spath.scene.{Scene, Viewpoint}

/**
 * A worker that renders a chunk of the final image by shooting rays through
 * the pixels it works on.
 *
 * @param left the x position of the left side of the area this worker renders
 * @param top the y position of the top side of the area this worker renders
 * @param width the width of the area this worker renders
 * @param height the height of the area this worker renders
 *
 * @param scene the scene that is rendered
 * @param random a provider of random values
 */
class TracingWorker(
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int,
    val tracer: Tracer,
    val scene: Scene,
    val target: RenderTarget,
    val random: DoubleSupplier) {

  val sampleStorage = new DiscreteSampleStorage(width, height)

  /**
   * Renders a pass and adds the color to the samples array
   */
  def render(view: Viewpoint, maxBounces: Int, passes: Int = 1, cpuSaturation: Float = 1): Unit = {
    val dWidth = target.width
    val dHeight = target.height
    val displayOffsetX = left + (random.getAsDouble - 0.5).toFloat
    val displayOffsetY = top + (random.getAsDouble - 0.5).toFloat

    val maxIndex = width * height
    var wait = 0L
    for (index ← 0 until maxIndex) {
      val x = index % width
      val y = index / width
      val rx = (2 * (x + displayOffsetX) - dWidth) / dWidth
      val ry = (dHeight - 2 * (y + displayOffsetY)) / dWidth
      val ray = scene.camera.createRay(view, random, rx, ry)
      for (_ <- 0 until passes) {
        val start = System.nanoTime

        val color = tracer.trace(
          ray = ray,
          scene = scene,
          maxBounces = maxBounces,
          random = random)

        sampleStorage.addSample(x, y, color, 1)
        
        if(cpuSaturation < 1) {
          val stop = System.nanoTime
          wait += ((stop - start) * (1 / cpuSaturation - 1)).toLong
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
   * Draws the current samples to the display
   */
  def draw(): Unit = {
    val maxIndex = width * height
    for (index ← 0 until maxIndex) {
      val x = index % width
      val y = index / width

      val color = sampleStorage.getColor(x, y)

      target.setPixel(x + left, y + top, color)
    }
  }
}
