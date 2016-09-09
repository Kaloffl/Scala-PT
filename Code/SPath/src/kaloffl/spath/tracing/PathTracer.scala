package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Ray, Vec2d, Vec3d}
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material

class PathTracer(maxBounces: Int) extends Tracer {

  override def trace(initialRay: Ray,
                     scene: Scene,
                     random: DoubleSupplier): Color = {
    var ray = initialRay
    var color = Color.White
    var i = 0

    // list of materials in which the rays entered
    var mediaIndex = scene.initialMediaStack.length - 1
    val media = new Array[Material](maxBounces + 1)
    System.arraycopy(scene.initialMediaStack, 0, media, 0, scene.initialMediaStack.length)

    while (i < maxBounces) {
      // russian roulett ray termination
      val survivability = Math.max(color.r2, Math.max(color.g2, color.b2)) * (maxBounces - i)
      if (survivability < 1) {
        if (random.getAsDouble > survivability) {
          return Color.Black
        } else {
          color /= survivability
        }
      }

      // First we determine the distance it will take the ray to hit an air 
      // particle and be scattered. If the current air has a scatter probability 
      // of 0, the distance will be infinity.
      val scatterChance = random.getAsDouble
      val scatterDist = -Math.log(1 - scatterChance) / media(mediaIndex).scatterProbability

      // Now try to find an object in the scene that is closer than the determined 
      // scatter depth. If none is found and the scatter depth is not infinity, 
      // the ray will be scattered.
      // The added epsilon value will help prevent rays scattering close to a 
      // surface and no properly intersecting with it afterwards.
      val intersection = scene.getIntersection(ray, scatterDist + 0.0001)
      if (!intersection.hitObject) {
        // if no object was hit, the ray will either scatter or hit the sky. At 
        // the moment the sky will only really work if the air is clear and the 
        // scatter probability is 0.
        if (java.lang.Double.isInfinite(scatterDist)) {
          val dist = scene.skyDistance
          val point = ray.atDistance(dist)
          val emitted = scene.skyMaterial.getEmittance(ray.normal)
          val absorbed =
            if (java.lang.Double.isInfinite(dist)) {
              Color.White
            } else {
              (media(mediaIndex).absorbtion * -dist.toFloat).exp
            }
          return color * emitted * absorbed
        }

        val point = ray.atDistance(scatterDist)
        val absorbed = (media(mediaIndex).absorbtion * -scatterDist.toFloat).exp
        ray = new Ray(point, Vec3d.randomNormal(Vec2d.random(random)))
        color *= absorbed
      } else {
        val depth = intersection.depth

        val absorbed = if(media(mediaIndex).absorbtion != Color.Black) {
        	  (media(mediaIndex).absorbtion * -depth.toFloat).exp
        } else {
          Color.White
        }

        if (intersection.material.emission != Color.Black) {
          return color * intersection.material.emission * absorbed
        }

        val point = ray.atDistance(depth)
    		val surfaceNormal = intersection.normal()
        val uv = intersection.textureCoordinate()
    		val (scatterings, weights) = intersection.material.getScattering(
    				incomingNormal = ray.normal,
    				surfaceNormal = surfaceNormal,
    				uv = uv,
    				outsideIor = media(mediaIndex).ior,
    				random = random)

        val newDir = {
          val rand = random.getAsDouble
          var i = 1
          var weightSum = weights(0)
          while(rand > weightSum && i < weights.length) {
            weightSum += weights(i)
            i += 1
          }
          scatterings(i - 1)
        }

        val bsdf = intersection.material.evaluateBSDF(-ray.normal, surfaceNormal, newDir, uv, media(mediaIndex).ior)
        if (newDir.dot(surfaceNormal) < 0) {
          // if the new ray has entered a surface
          mediaIndex += 1
          media(mediaIndex) = intersection.material
        } else if (ray.normal.dot(surfaceNormal) >= 0) {
          // if the ray is exiting a surface
          // TODO unfortunately in some edge cases the tracer thinks the ray
          // exited an object and pops the media stack early. So when the real
          // medium is exited it tries to exit again, leaving an empty stack
          // behind. So the solution for now is to ignore those cases because
          // they contribute almost nothing. A better solution will be to fix
          // the exiting detection.
          mediaIndex = Math.max(0, mediaIndex - 1)
        }
        ray = new Ray(point, newDir)
        color *= bsdf * absorbed
      }
      i += 1
    }
    return Color.Black
  }
}