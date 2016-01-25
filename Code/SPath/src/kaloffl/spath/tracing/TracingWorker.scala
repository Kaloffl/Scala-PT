package kaloffl.spath.tracing

import java.util.function.DoubleSupplier
import kaloffl.spath.RenderTarget
import kaloffl.spath.math.Color

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
    val random: DoubleSupplier) {

  // The sum of determined colors is stored in this array
  // r1, g1, b1, r2, g2, b2, ...
  val samples = new Array[Float](width * height * 3)

  // the number of passes that have been rendered
  var samplesTaken: Int = 0
  var done = false

  def sampleToDistribution(s: Int): Float = {
    return (random.getAsDouble - 0.5).toFloat
  }

  /**
   * Renders a pass and adds the color to the samples array
   */
  def render(maxBounces: Int, pass: Int, display: RenderTarget): Unit = {
    if (done) return

    samplesTaken += 1

    val dWidth = display.width
    val dHeight = display.height
    val r = Math.sqrt(pass).toInt
    val dx = sampleToDistribution(Math.min(r, pass - r * r))
    val dy = sampleToDistribution(Math.min(r, r * r + 2 * r - pass))
    val displayOffsetX = dWidth * 0.5f + dx - left
    val displayOffsetY = dHeight * 0.5f + dy - top

    val maxIndex = width * height
    var difference = 0f
    for (index ← 0 until maxIndex) {
      val x = (index % width - displayOffsetX) / dHeight
      val y = (displayOffsetY - index / width) / dHeight
      val color = tracer.trace(x, y, maxBounces, random)
      val sampleIndex = index * 3

      val prevSample = samplesTaken - 1
      difference += Math.abs(color.r2 - samples(sampleIndex) / prevSample)
      difference += Math.abs(color.g2 - samples(sampleIndex + 1) / prevSample)
      difference += Math.abs(color.b2 - samples(sampleIndex + 2) / prevSample)

      samples(sampleIndex) += color.r2
      samples(sampleIndex + 1) += color.g2
      samples(sampleIndex + 2) += color.b2
    }
    //    if (difference / maxIndex < 1 / 255f) {
    //      done = true
    //    }
  }

  /**
   * Draws the current samples to the display
   */
  def draw(display: RenderTarget) {
    val maxIndex = width * height
    for (index ← 0 until maxIndex) {
      val x = index % width + left
      val y = index / width + top
      val i3 = index * 3

      // we need to use the new-constructor here because the values are already
      // in squared space
      val color = new Color(
        samples(i3) / samplesTaken,
        samples(i3 + 1) / samplesTaken,
        samples(i3 + 2) / samplesTaken)

      display.setPixel(x, y, color)
    }
  }
}
