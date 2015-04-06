package kaloffl.spath.tracing

import kaloffl.spath.Display
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene

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
    val random: () ⇒ Double) {

  // The sum of determined colors is stored in this array per-channel-per-pixel
  val samples: Array[Float] = new Array[Float](width * height * 3)
  val camera: Camera = scene.camera

  // the number of passes that have been rendered
  var samplesTaken: Int = 0

  /**
   * Renders a pass and adds the color to the samples array
   */
  def render(maxBounces: Int, pass: Int, display: Display): Unit = {
    samplesTaken += 1

    val dWidth = display.width
    val dHeight = display.height
    val displayOffsetX = dWidth * 0.5f + random() * 2.0f - 1.0f - left
    val displayOffsetY = dHeight * 0.5f + random() * 2.0f - 1.0f - top
    val context = new Context(random, pass, maxBounces, display)

    val maxIndex = width * height
    for (index ← 0 until maxIndex) {
      val x = (index % width - displayOffsetX) / dHeight
      val y = (displayOffsetY - index / width) / dHeight
      val ray = camera.createRay(random, x, y)

      val color = pathTrace(ray, context)

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
    var light = Color.BLACK
    var index = 0
    val lightCount = scene.lightShapes.length
    while (index < lightCount) {
      val shape = scene.lightShapes(index)
      val point = shape.getRandomInnerPoint(random)
      val dir = (point - pos).normalize
      // Checking the normal against the direction to the light to prevent rays
      // going through the surface of the object the point sits on
      val diffuse = normal.dot(dir)
      if (diffuse >= 0) {
        val intersection = scene.getIntersection(new Ray(pos, dir))
        if (intersection.hitShape == shape) {
          val worldPos = pos + dir * intersection.depth
          val surfaceNormal = shape.getNormal(worldPos)
          val emittance = intersection.material.reflectanceAt(worldPos, surfaceNormal, context)
          light += emittance * diffuse.toFloat
        }
      }
      index += 1
    }
    return light / lightCount
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
  def pathTrace(startRay: Ray, context: Context): Color = {
    var bounce = 0
    var color = Color.WHITE
    var ray = startRay
    val bounces = context.maxBounces
    val rouletThreshold = bounces * 0.9f
    val killThreshold = 1.0f / Math.max((bounces * 0.1f).toInt, 1)

    while (bounce < bounces) {
      bounce += 1

      val intersection = scene.getIntersection(ray)
      val hitShape = intersection.hitShape
      if (null == hitShape) return color * skyColor(ray.normal)

      val material = intersection.material

      val point = ray.normal * intersection.depth + ray.start
      val surfaceNormal = hitShape.getNormal(point)

      color = color * material.reflectanceAt(point, surfaceNormal, context)

      if (material.terminatesPath) return color
      if (bounce == bounces) return Color.BLACK

      // The more bounces the ray went the higher the chance is that we will just
      // calculate the direct light and stop it. This way we reduce rendering time
      // and create a brighter image.
      if (bounce > rouletThreshold && random() < killThreshold)
        return directLight(point, surfaceNormal, context) * color

      val newDir = material.reflectNormal(point, surfaceNormal, ray.normal, context)
      ray = new Ray(point, newDir)
    }
    return Color.BLACK
  }
}
