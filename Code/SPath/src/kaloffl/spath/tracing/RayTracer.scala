package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Ray}
import kaloffl.spath.scene.Scene

object RayTracer extends Tracer {

  override def trace(ray: Ray,
                     scene: Scene,
                     maxBounces: Int,
                     random: DoubleSupplier): (Color, Int) = {
    if (0 == maxBounces) return (Color.Black, 1)

    val intersection = scene.getIntersection(ray, Double.PositiveInfinity)
    if (!intersection.hitObject) {
      val dist = scene.skyDistance
      val point = ray.atDistance(dist)
      return (scene.skyMaterial.getEmittance(ray.normal), 1)
    } else {
      val depth = intersection.depth

      if (intersection.material.emission != Color.Black) {
        return (intersection.material.emission, 1)
      }

      val point = ray.atDistance(depth)
      val surfaceNormal = intersection.normal()
      val scatterings = intersection.material.getScattering(
        incomingNormal = ray.normal,
        surfaceNormal = surfaceNormal,
        uv = intersection.textureCoordinate(),
        outsideIor = 1,
        random = random)

      var color = Color.Black
      val hints = scene.lightHints
      var h = 0
      while (h < hints.length) {
        val hint = hints(h)
        if (hint.applicableFor(point)) {
          val lightRay = hint.target.createRandomRay(point, random)
          val contribution = surfaceNormal.dot(lightRay.normal).toFloat
          if (contribution > 0) {
            val angle = hint.target.getSolidAngle(point).toFloat
            val lightIntersection = scene.getIntersection(lightRay, Double.PositiveInfinity)
            color += lightIntersection.material.emission * angle * contribution
          }
        }
        h += 1
      }
      var i = 0
      while (i < scatterings.length) {
        color *= scatterings(i).color
        i += 1
      }
      return (color, 1)
    }
  }
}