package kaloffl.spath.tracing

import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.math.Color
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.math.Ray

class PathTracer(val scene: Scene) extends Tracer {

  override def trace(startRay: Ray,
                     maxBounces: Int,
                     startMedium: Material,
                     context: Context): Color = {
    var color = Color.WHITE
    var ray = startRay
    var i = 0

    // list of materials in which the rays entered
    var mediaIndex = 0
    val media = new Array[Material](maxBounces)
    media(0) = startMedium

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
      val scatterDist = -Math.log(1 - scatterChance) / media(mediaIndex).scatterPropability

      // Now try to find an object in the scene that is closer than the determined 
      // scatter depth. If none is found and the scatter depth is not infinity, 
      // the ray will be scattered.
      val intersection = scene.getIntersection(ray, scatterDist)
      if (!intersection.hitObject) {
        // if no object was hit, the ray will either scatter or hit the sky. At 
        // the moment the sky will only really work if the air is clear and the 
        // scatter probability is 0.
        if (java.lang.Double.isInfinite(scatterDist)) {
          // FIXME Rays hitting the corner of a room and then not hitting the wall 
          // because they are too close are escaping the scene.
          val dist = scene.skyDistance
          val point = ray.atDistance(dist)
          val emitted = scene.skyMaterial.getEmittance(
            point, -ray.normal, ray.normal, dist, context)
          val absorbtionScale = if (java.lang.Double.isInfinite(dist)) {
            0.0f
          } else {
            (media(mediaIndex).absorbtionCoefficient * -dist).toFloat
          }
          val absorbed = (media(mediaIndex).getAbsorbtion(point, context) * absorbtionScale).exp
          return color * emitted * absorbed
        }

        val point = ray.atDistance(scatterDist)
        val absorbed = (media(mediaIndex).getAbsorbtion(point, context)
          * (media(mediaIndex).absorbtionCoefficient * -scatterDist).toFloat).exp
        ray = new Ray(point, Vec3d.randomNormal(Vec2d.random(context.random)))
        color *= absorbed
      } else {
        val depth = intersection.depth
        val point = ray.atDistance(depth)
        val surfaceNormal = intersection.shape.getNormal(point)
        val info = intersection.material.getInfo(point, surfaceNormal, ray.normal, depth, media(mediaIndex).refractivityIndex, context)
        val absorbed = (media(mediaIndex).getAbsorbtion(point, context) * (media(mediaIndex).absorbtionCoefficient * -depth).toFloat).exp

        if (info.emittance != Color.BLACK) {
          return color * info.emittance * absorbed
        }

        val newDir = info.outgoing

        if (newDir.dot(surfaceNormal) < 0) {
          // if the new ray has entered a surface
          mediaIndex += 1
          media(mediaIndex) = intersection.material
        } else if (ray.normal.dot(surfaceNormal) >= 0) {
          // if the ray is exiting a surface
          // TODO unfortunately in some edge cases the tracer things the ray 
          // exited an object and pops the media stack early. So when the real
          // medium is exited it tries to exit again, leaving an empty stack
          // behind. So the solution for now is to ignore those cases because
          // they contribute almost nothing. A better solution will be to fix
          // the exiting detection.
          mediaIndex = Math.max(0, mediaIndex - 1)
        }
        ray = new Ray(point, newDir)
        color *= info.reflectance * absorbed
      }
      i += 1
    }
    return Color.BLACK
  }
}