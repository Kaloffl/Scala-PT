package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Ray}
import kaloffl.spath.scene.Scene

object RayTracer extends Tracer {

  override def trace(ray: Ray,
                     scene: Scene,
                     maxBounces: Int,
                     random: DoubleSupplier): Color = {
    if (0 == maxBounces) return Color.Black

    val intersection = scene.getIntersection(ray, Double.PositiveInfinity)
    if (!intersection.hitObject) {
      val dist = scene.skyDistance
      val point = ray.atDistance(dist)
      return scene.skyMaterial.getEmittance(ray.normal)
    } else {
      val depth = intersection.depth

      if (intersection.material.emission != Color.Black) {
        return intersection.material.emission
      }

      val point = ray.atDistance(depth)
      val surfaceNormal = intersection.normal()
      val uv = intersection.textureCoordinate()
      val scatterings = intersection.material.getScattering(
        incomingNormal = ray.normal,
        surfaceNormal = surfaceNormal,
        uv = uv,
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
            val bsdf = intersection.material.evaluateBSDF(-ray.normal, surfaceNormal, lightRay.normal, uv, 1.0f)
            val angle = hint.target.getSolidAngle(point).toFloat
            val lightIntersection = scene.getIntersection(lightRay, Double.PositiveInfinity)
            color += lightIntersection.material.emission * angle * contribution * bsdf
          }
        }
        h += 1
      }
      return color
    }
  }
}