package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Color
import kaloffl.spath.math.Ray
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material

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

      if (intersection.material.emittance != Color.Black) {
        return intersection.material.emittance
      }

      val point = ray.atDistance(depth)
      val surfaceNormal = intersection.normal()
      val info = intersection.material.getInfo(
        incomingNormal = ray.normal,
        surfaceNormal = surfaceNormal,
        textureCoordinate = intersection.textureCoordinate(),
        airRefractiveIndex = 1,
        random = random)

      val scattering = info.scattering
      var color = Color.Black
      val hints = scene.lightHints
      var h = 0
      while (h < hints.length) {
        val hint = hints(h)
        if (hint.applicableFor(point)) {
          val lightRay = hint.target.createRandomRay(point, random)
          val contribution = scattering.getContribution(lightRay.normal)
          if (contribution > 0) {
            val angle = hint.target.getSolidAngle(point).toFloat
            val lightIntersection = scene.getIntersection(lightRay, Double.PositiveInfinity)
            color += lightIntersection.material.emittance * angle * contribution
          }
        }
        h += 1
      }
      return info.reflectance * color
    }
  }
}