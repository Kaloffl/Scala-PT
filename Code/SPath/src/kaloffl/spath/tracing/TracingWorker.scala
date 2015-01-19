package kaloffl.spath.tracing

import kaloffl.spath.Display
import kaloffl.spath.math.Vec3f
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.shapes.Shape

class TracingWorker(
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int,
    val scene: Scene,
    val random: () ⇒ Float) {

  val samples: Array[Float] = new Array[Float](width * height * 3)
  val camera: Camera = scene.camera

  var samplesTaken: Int = 0

  def render(maxBounces: Int, dWidth: Int, dHeight: Int): Unit = {
    samplesTaken += 1

    val maxIndex = width * height
    for (index ← 0 until maxIndex) {
      val x: Int = index % width + left
      val y: Int = index / width + top

      val ray = camera.createRay(random, x, y, dWidth, dHeight)

      val color = pathTrace(ray, maxBounces)

      val sampleIndex = index * 3
      samples(sampleIndex) += color.x
      samples(sampleIndex + 1) += color.y
      samples(sampleIndex + 2) += color.z
    }
  }

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

  def pathTrace(ray: Ray, bounce: Int): Vec3f = {

    val intersection = scene.getIntersection(ray)
    if (null == intersection) return Vec3f.BLACK

    val hitObject = intersection.hitObject
    val material = hitObject.material
    val emittance = material.emittance
    if (emittance.lengthSq > 0.0f) return emittance

    if (bounce <= 0) return Vec3f.BLACK

    val point = ray.normal * intersection.depth + ray.start
    val normal = hitObject.shape.getNormal(point)

    val reflectivity = material.reflectivity
    val refractivity = material.refractivity
    val glossiness = material.glossiness
    val diffuse = if (material.reflectance.lengthSq > 0.0f) 1 else 0

    val sum = reflectivity + refractivity + diffuse
    val color = (random() * sum) match {
      case f if (f < reflectivity) ⇒ {
        val dir =
          if (glossiness > 0.0f) {
            (ray.normal.reflect(normal) + normal.randomHemisphere(random) * glossiness).normalize
          } else {
            ray.normal.reflect(normal)
          }
        pathTrace(new Ray(point, dir), bounce - 1)
      }
      case f if (f < reflectivity + refractivity) ⇒ {
        val dir =
          if (glossiness > 0.0f) {
            (ray.normal.refract(normal, 1.0f, material.refractivityIndex) + normal.randomHemisphere(random) * glossiness).normalize
          } else {
            ray.normal.refract(normal, 1.0f, material.refractivityIndex)
          }
        pathTrace(new Ray(point, dir), bounce - 1)
      }
      case _ ⇒ {
        val dir = normal.randomHemisphere(random)
        material.reflectance * Math.abs(normal.dot(dir)) * pathTrace(new Ray(point, dir), bounce - 1)
      }
    }
    return color * sum
  }
}