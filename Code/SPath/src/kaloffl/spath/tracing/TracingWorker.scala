package kaloffl.spath.tracing

import java.util.function.DoubleSupplier
import kaloffl.spath.RenderTarget
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.shapes.Shape

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
    val scene: Scene,
    val random: DoubleSupplier) {

  // The sum of determined colors is stored in this array
  // r1, g1, b1, r2, g2, b2, ...
  val samples = new Array[Float](width * height * 3)
  val tracer = new PathTracer(scene)

  // the number of passes that have been rendered
  var samplesTaken: Int = 0
  var done = false

  def sampleToDistribution(s: Int): Double = {
    return random.getAsDouble - 0.5
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
    val displayOffsetX = dWidth * 0.5 + dx - left
    val displayOffsetY = dHeight * 0.5 + dy - top
    val context = new Context(random, pass, maxBounces, display)

    val maxIndex = width * height
    var difference = 0f
    for (index ← 0 until maxIndex) {
      val x = (index % width - displayOffsetX) / dHeight
      val y = (displayOffsetY - index / width) / dHeight
      val ray = scene.camera.createRay(random, x, y)
      val color = tracer.trace(ray, maxBounces, scene.airMedium, context)
      val sampleIndex = index * 3

      val prevSample = samplesTaken - 1
      difference += Math.abs(color.r2 - samples(sampleIndex) / prevSample)
      difference += Math.abs(color.g2 - samples(sampleIndex + 1) / prevSample)
      difference += Math.abs(color.b2 - samples(sampleIndex + 2) / prevSample)

      samples(sampleIndex) += color.r2
      samples(sampleIndex + 1) += color.g2
      samples(sampleIndex + 2) += color.b2
    }
    if (difference / maxIndex < 1 / 255f) {
      done = true
    }
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

      def toColorChannelInt(value: Double): Int = {
        (Math.min(Math.sqrt(value / samplesTaken), 1) * 0xff).toInt
      }
      if (samples(i3).isNaN) {
        System.err.println("NaN detected! ABORT SHIP!")
      }

      val red = toColorChannelInt(samples(i3))
      val green = toColorChannelInt(samples(i3 + 1))
      val blue = toColorChannelInt(samples(i3 + 2))
      val color = 0xff << 24 | red << 16 | green << 8 | blue

      display.setPixel(x, y, color)
    }
  }
}
