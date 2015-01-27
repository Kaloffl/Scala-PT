package kaloffl.spath.tracing

import kaloffl.spath.Display
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.shapes.Shape

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
 *
 * * @author Lars Donner
 */
class TracingWorker(
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int,
    val scene: Scene,
    val random: () ⇒ Float) {

  // The sum of determined colors is stored in this array per-channel-per-pixel
  val samples: Array[Double] = new Array[Double](width * height * 3)
  val camera: Camera = scene.camera

  // the number of passes that have been rendered
  var samplesTaken: Int = 0

  /**
   * Renders a pass and adds the color to the samples array
   */
  def render(maxBounces: Int, dWidth: Int, dHeight: Int): Unit = {
    samplesTaken += 1

    val maxIndex = width * height
    for (index ← 0 until maxIndex) {
      val x: Int = index % width + left
      val y: Int = index / width + top

      val ray = camera.createRay(random, x, y, dWidth, dHeight)

      val result = pathTrace(ray, maxBounces)
      // gamma correction:
      val color = Vec3d(
        Math.pow(result.x, 0.45),
        Math.pow(result.y, 0.45),
        Math.pow(result.z, 0.45))

      val sampleIndex = index * 3
      samples(sampleIndex) += color.x
      samples(sampleIndex + 1) += color.y
      samples(sampleIndex + 2) += color.z
    }
  }

  /**
   * Draws the current samples to the display
   */
  def draw(display: Display) {
    val scaling: Float = 255f / samplesTaken
    val maxIndex = width * height
    for (index ← 0 until maxIndex) {
      val x = index % width + left
      val y = index / width + top
      val i3 = index * 3

      val red = Math.min((samples(i3) * scaling).toInt, 0xff)
      val green = Math.min((samples(i3 + 1) * scaling).toInt, 0xff)
      val blue = Math.min((samples(i3 + 2) * scaling).toInt, 0xff)
      val color = red << 16 | green << 8 | blue

      display.drawPixel(x, y, color)
    }
  }

  /**
   * Tries to find direct lighting of a point in space by targeting random
   * points in the lights of the scene and tracing to them.
   */
  def directLight(pos: Vec3d): Vec3d = {
    val lights = scene.lights

    var i = 0
    var light = Vec3d.BLACK
    while (i < lights.length) {
      val point = lights(i).shape.getRandomInnerPoint(random)
      val intersection = scene.getIntersection(new Ray(pos, (point - pos).normalize))
      if (null != intersection) {
        light += intersection.hitObject.material.emittance
      }
      i += 1
    }
    return light
  }

  /**
   * Traces a ray in the scene and reacts to intersections depending on the
   * material that was hit.
   */
  def pathTrace(ray: Ray, bounce: Int): Vec3d = {

    val intersection = scene.getIntersection(ray)
    if (null == intersection) return Vec3d.BLACK

    val hitObject = intersection.hitObject
    val material = hitObject.material
    val emittance = material.emittance
    if (emittance.lengthSq > 0.0) return emittance

    if (bounce <= 0) return Vec3d.BLACK

    val point = ray.normal * intersection.depth + ray.start

    // The more bounces the ray went the higher the chance is that we will just
    // calculate the direct light and stop it. This way we reduce rendering time
    // and create a brighter image.
    if (random.apply() > bounce / 16.0) return directLight(point) * material.reflectance

    val normal = hitObject.shape.getNormal(point)

    // Depending on the material, we randomly choose to reflect, refract or diffuse bounce the next ray.
    val sum = material.reflectivity + material.refractivity + (if (material.reflectance.lengthSq > 0.0f) 1 else 0)
    val indirectLight = (random() * sum) match {
      case f if (f < material.reflectivity) ⇒ {
        val dir =
          if (material.glossiness > 0.0f) {
            (ray.normal.reflect(normal) + normal.randomHemisphere(random) * material.glossiness).normalize
          } else {
            ray.normal.reflect(normal)
          }
        pathTrace(new Ray(point, dir), bounce - 1)
      }
      case f if (f < material.reflectivity + material.refractivity) ⇒ {
        val dir =
          if (material.glossiness > 0.0f) {
            (ray.normal.refract(normal, 1.0f, material.refractivityIndex) + normal.randomHemisphere(random) * material.glossiness).normalize
          } else {
            ray.normal.refract(normal, 1.0f, material.refractivityIndex)
          }
        pathTrace(new Ray(point, dir), bounce - 1)
      }
      case _ ⇒ {
        val dir = normal.randomHemisphere(random)
        pathTrace(new Ray(point, dir), bounce - 1) * material.reflectance * Math.abs(normal.dot(dir))
      }
    }
    return indirectLight
  }
}