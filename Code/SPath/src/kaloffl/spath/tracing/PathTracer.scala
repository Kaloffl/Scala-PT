package kaloffl.spath.tracing

import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.math.Color
import kaloffl.spath.scene.Scene

class PathTracer(val scene: Scene, context: Context) extends Tracer {

  override def trace(startRay: Ray, maxBounces: Int): Color = {
        var color = Color.WHITE
    var ray = startRay
    var air = scene.air
    var i = 0
    while (i < maxBounces) {
      // russian roulett ray termination
      val survivability = Math.min(1, Math.max(color.r2, Math.max(color.g2, color.b2)) * (maxBounces - i))
      if (context.random.getAsDouble > survivability) {
        return Color.BLACK
      } else {
        color /= survivability
      }

      // First we determine the distance it will take the ray to hit an air 
      // particle and be scattered. If the current air has a scatter probability 
      // of 0, the distance will be infinity.
      val scatterChance = context.random.getAsDouble
      val scatterDist = Math.log(scatterChance + 1) / air.scatterPropability

      // Now try to find an object in the scene that is closer than the determined 
      // scatter depth. If none is found and the scatter depth is not infinity, 
      // the ray will be scattered.
      val intersection = scene.getIntersection(ray, scatterDist)
      if (null == intersection) {
        // if no object was hit, the ray will either scatter or hit the sky. At 
        // the moment the sky will only really work if the air is clear and the 
        // scatter probability is 0.
        if (java.lang.Double.isInfinite(scatterDist)) {
          // FIXME Rays hitting the corner of a room and then not hitting the wall 
          // because they are too close are escaping the scene.
          val dist = scene.skyDistance
          val point = ray.start + ray.normal * dist
          val emitted = scene.sky.getEmittance(
            point, -ray.normal, ray.normal, dist, context)
          val absorbtionScale = if (java.lang.Double.isInfinite(dist)) {
            0.0f
          } else {
            (air.absorbtionCoefficient * -dist).toFloat
          }
          val absorbed = (air.getAbsorbtion(point, context) * absorbtionScale).exp
          return color * emitted * absorbed
        }

        val point = ray.start + ray.normal * scatterDist
        val absorbed = (air.getAbsorbtion(point, context)
          * (air.absorbtionCoefficient * -scatterDist).toFloat).exp
        ray = new Ray(point, Vec3d.randomNormal(Vec2d.random(context.random)))
        color *= absorbed
      } else {
        val depth = intersection.depth
        val point = ray.normal * depth + ray.start
        val surfaceNormal = intersection.surfaceNormal
        val info = intersection.material.getInfo(point, surfaceNormal, ray.normal, depth, air.refractivityIndex, context)
        val absorbed = (air.getAbsorbtion(point, context) * (air.absorbtionCoefficient * -depth).toFloat).exp

        if (info.emittance != Color.BLACK) {
          return color * info.emittance * absorbed
        }

        val newDir = info.outgoing
        val diffuse = newDir.dot(surfaceNormal)
        if (diffuse < 0) {
          air = intersection.material
        } else {
          air = scene.air
        }
        ray = new Ray(point, newDir)
        if (intersection.material.isInstanceOf[DiffuseMaterial]) {
          color *= info.reflectance * absorbed * diffuse.toFloat * 2
        } else {
          color *= info.reflectance * absorbed
        }
      }
      i += 1
    }
    return Color.BLACK
  }
}