package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Ray}
import kaloffl.spath.scene.Scene

class RayTracer(maxBounces: Int) extends Tracer {

  override def trace(ray: Ray,
                     scene: Scene,
                     random: DoubleSupplier): Color = {

    val intersection = scene.getIntersection(ray, Double.PositiveInfinity)
    if (!intersection.hitObject) {
      return scene.skyMaterial.getEmittance(ray.normal)
    } else {
      val depth = intersection.depth

      if (intersection.material.emission != Color.Black) {
        return intersection.material.emission
      }

      val point = ray.atDistance(depth)
      val surfaceNormal = intersection.normal()
      val uv = intersection.textureCoordinate()

      var color = Color.Black
      val hints = scene.lightHints
      var h = 0
      while (h < hints.length) {
        val hint = hints(h)
        if (hint.applicableFor(point)) {
          val lightDir = hint.sampler.getDirection(
            position = point,
            incomingNormal = ray.normal,
            surfaceNormal = surfaceNormal,
            uv = uv,
            outsideIor = 1.0f,
            random = random)
          val contribution = surfaceNormal.dot(lightDir).toFloat
          if (contribution > 0) {
            val bsdf = intersection.material.evaluateBSDF(-ray.normal, surfaceNormal, lightDir, uv, 1.0f)
            val angle = hint.sampler.getPropability(
              position = point,
              incomingNormal = ray.normal,
              surfaceNormal = surfaceNormal,
              outgoingNormal = lightDir,
              uv = uv,
              outsideIor = 1.0f)
            val lightRay = new Ray(point, lightDir)
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