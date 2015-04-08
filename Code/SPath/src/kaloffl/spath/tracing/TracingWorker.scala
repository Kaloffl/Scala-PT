package kaloffl.spath.tracing

import kaloffl.spath.Display
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import java.util.function.DoubleSupplier

/**
 * A worker that renders a chink of the final image by shooting rays through
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

  // the number of passes that have been rendered
  var samplesTaken: Int = 0

  def sampleToDistribution(s: Int): Double = {
      if (0 == s) return 1
      val n = s / 2 + 2 * (Math.log(s / 2 + 2) / Math.log(2) - 1).toInt
      val p = Math.pow(2, (Math.log(n + 1) / Math.log(2)).toInt)
      return (n + 1 - p) / (p + 1) * (2 * (s & 0x01) - 1)
    }

  /**
   * Renders a pass and adds the color to the samples array
   */
  def render(maxBounces: Int, pass: Int, display: Display): Unit = {
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

      val color = pathTrace(ray, maxBounces, context)

      val sampleIndex = index * 3
      samples(sampleIndex) += color.r2
      samples(sampleIndex + 1) += color.g2
      samples(sampleIndex + 2) += color.b2
    }
  }

  /**
   * Draws the current samples to the display
   */
  def draw(display: Display) {
    val maxIndex = width * height
    for (index ← 0 until maxIndex) {
      val x = index % width + left
      val y = index / width + top
      val i3 = index * 3

      def toColorChannelInt(value: Double): Int = {
        (Math.min(Math.sqrt(value / samplesTaken), 1) * 0xff).toInt
      }

      val red = toColorChannelInt(samples(i3))
      val green = toColorChannelInt(samples(i3 + 1))
      val blue = toColorChannelInt(samples(i3 + 2))
      val color = red << 16 | green << 8 | blue

      display.drawPixel(x, y, color)
    }
  }

  /**
   * Tries to find direct lighting of a point in space by targeting random
   * points in the lights of the scene and tracing to them.
   */
  def directLight(pos: Vec3d, normal: Vec3d, context: Context): Color = {
    // TODO several problems with this method: 
    // 1. Light shapes with a larger volume have a higher chance of being missed
    // despite having the same surface area exposed.
    // 2. Light traveling through transparent shapes is ignored, all shapes
    // count as blocking.
    val shape = scene.getRandomLight(random)
    val point = shape.getRandomInnerPoint(random)
    val dir = (point - pos).normalize
    // Checking the normal against the direction to the light to prevent rays
    // going through the surface of the object the point sits on
    val diffuse = normal.dot(dir)
    if (diffuse >= 0) {
      val intersection = scene.getIntersection(new Ray(pos, dir))
      val hit = intersection.hitShape
      if (null != hit) {
        val depth = intersection.depth
        val worldPos = pos + dir * depth
        val surfaceNormal = hit.getNormal(worldPos)
        val emittance = intersection.material.emittanceAt(worldPos, surfaceNormal, context)
        val attenuation = intersection.material.attenuation(depth)

        return emittance * (diffuse * attenuation).toFloat
      }
    }
    return Color.BLACK
  }

  /**
   * @return a color that might depend on the incoming normal
   */
  def skyColor(normal: Vec3d): Color = {
    return Color.BLACK
  }

  /**
   * Traces a ray in the scene and reacts to intersections depending on the
   * material that was hit.
   */
  def pathTrace(ray: Ray, bouncesLeft: Int, context: Context): Color = {
    if (0 == bouncesLeft) return Color.BLACK

    val intersection = scene.getIntersection(ray)
    val hitShape = intersection.hitShape
    if (null == hitShape) return skyColor(ray.normal)

    val depth = intersection.depth
    val point = ray.normal * depth + ray.start
    val surfaceNormal = hitShape.getNormal(point)

    val info = intersection.material.getInfo(point, surfaceNormal, ray.normal, context)
    if (info.emittance != Color.BLACK) {
      return info.emittance * info.attenuation(depth).toFloat
    }

    //    return Color(surfaceNormal)
    //    return info.reflectance * directLight(point, surfaceNormal, context)

    val newDir = info.outgoing
    val indirect = pathTrace(new Ray(point, newDir), bouncesLeft - 1, context)
    val color = info.reflectance
    if (info.translucent) {
      return color * indirect
    }
    val direct = directLight(point, surfaceNormal, context)
    return color * (direct + indirect)
  }
}
