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

  // The sum of determined colors is stored in this array per-channel-per-pixel
  val samples: Array[Float] = new Array[Float](width * height * 3)
  val camera: Camera = scene.camera
  val tracer = new PathTracer(scene)
  //  val tracer = new BidirectionalPathTracer(scene)

  // the number of passes that have been rendered
  var samplesTaken: Int = 0

  def sampleToDistribution(s: Int): Double = {
    val max = Math.sqrt(3000)
    return (s + random.getAsDouble - 0.5) / max

    //    if (left + width > 640) return 0 //random.getAsDouble
    //
    //    if (0 == s) return 1
    //    val n = s / 2 + 2 * (Math.log(s / 2 + 2) / Math.log(2) - 1).toInt
    //    val p = Math.pow(2, (Math.log(n + 1) / Math.log(2)).toInt)
    //    return (n + 1 - p) / (p + 1) * (2 * (s & 0x01) - 1)
  }

  /**
   * Renders a pass and adds the color to the samples array
   */
  def render(maxBounces: Int, pass: Int, display: RenderTarget): Unit = {
    samplesTaken += 1

    val dWidth = display.width
    val dHeight = display.height
    val r = Math.sqrt(pass).toInt
    val dx = sampleToDistribution(Math.min(r, pass - r * r))
    val dy = sampleToDistribution(Math.min(r, r * r + 2 * r - pass))
    val displayOffsetX = dWidth * 0.5f + dx - left
    val displayOffsetY = dHeight * 0.5f + dy - top
    val context = new Context(random, pass, maxBounces, display)

    val maxIndex = width * height
    for (index ← 0 until maxIndex) {
      val x = (index % width - displayOffsetX) / dHeight
      val y = (displayOffsetY - index / width) / dHeight
      val ray = camera.createRay(random, x, y)

      val color = tracer.trace(ray, maxBounces, scene.air, context)
      val sampleIndex = index * 3
      samples(sampleIndex) += color.r2
      samples(sampleIndex + 1) += color.g2
      samples(sampleIndex + 2) += color.b2
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
      val color = red << 16 | green << 8 | blue

      display.setPixel(x, y, color)
    }
  }
}
