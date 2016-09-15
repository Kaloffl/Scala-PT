package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Ray, Vec2d, Vec3d}
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material

class NaivePathTracer(maxBounces: Int) extends Tracer {

  override def trace(ray: Ray,
    scene: Scene,
    random: DoubleSupplier): Color = {
    val mediaStack = scene.initialMediaStack
    val mediaHead = mediaStack.length - 1
    return trace(ray, scene, mediaStack, mediaHead, 0, maxBounces, random)
  }

  def trace(
    ray: Ray,
    scene: Scene,
    media: Array[Material],
    mediaHead: Int,
    i: Int,
    maxBounces: Int,
    random: DoubleSupplier): Color = {
    if (i >= maxBounces) return Color.Black

    // First we determine the distance it will take the ray to hit an air 
    // particle and be scattered. If the current air has a scatter probability 
    // of 0, the distance will be infinity.
    val scatterChance = random.getAsDouble
    val scatterDist = -Math.log(1 - scatterChance) / media(mediaHead).scatterProbability

    // Now try to find an object in the scene that is closer than the determined 
    // scatter depth. If none is found and the scatter depth is not infinity, 
    // the ray will be scattered.
    // The added epsilon value will help prevent rays scattering close to a 
    // surface and no properly intersecting with it afterwards.
    val intersection = scene.getIntersection(ray, scatterDist /* + 0.0001*/ )
    if (!intersection.hitObject) {
      // if no object was hit, the ray will either scatter or hit the sky. At 
      // the moment the sky will only really work if the air is clear and the 
      // scatter probability is 0.
      if (java.lang.Double.isInfinite(scatterDist)) {
        val dist = scene.skyDistance
        val emitted = scene.skyMaterial.getEmittance(ray.normal)
          if (java.lang.Double.isInfinite(dist)) {
            return emitted
          } else {
            return emitted * (media(mediaHead).absorbtion * -dist.toFloat).exp
          }
      }

      val point = ray.atDistance(scatterDist)
      val absorbed = (media(mediaHead).absorbtion * -scatterDist.toFloat).exp
      val randomRay = new Ray(point, Vec3d.randomNormal(Vec2d.random(random)))
      val indirect = trace(randomRay, scene, media, mediaHead, i + 1, maxBounces, random)
      return absorbed * indirect
    } else {
      val depth = intersection.depth
      val absorbed = if (media(mediaHead).absorbtion != Color.Black) {
        (media(mediaHead).absorbtion * -depth.toFloat).exp
      } else {
        Color.White
      }

      if (intersection.material.emission != Color.Black) {
        return intersection.material.emission * absorbed
      }
      val point = ray.atDistance(depth)

      val surfaceNormal = intersection.normal()
      val inDir = ray.normal.dot(surfaceNormal)
      val uv = intersection.textureCoordinate()
      val otherIor =
        if (inDir < 0) {
          media(mediaHead).ior
        } else if (media(mediaHead) == intersection.material) {
          media(mediaHead - 1).ior
        } else {
          media(mediaHead).ior
        }

      val numSamples = 2
      var color = Color.Black
      var d = 0
      while (d < numSamples) {
        val newDir = surfaceNormal.randomHemisphere(Vec2d.random(random))
        val bsdf = intersection.material.evaluateBSDF(
          toEye = -ray.normal,
          surfaceNormal = surfaceNormal,
          toLight = newDir,
          uv = uv,
          outsideIor = otherIor)

        val newRay = new Ray(point, newDir)
        val outDir = newDir.dot(surfaceNormal)
        var newMedia = media
        var newHead = mediaHead
        if (inDir < 0 && outDir < 0) {
          // if the new ray has entered a surface
          newHead = mediaHead + 1
          newMedia = new Array[Material](newHead + 1)
          System.arraycopy(media, 0, newMedia, 0, newHead)
          newMedia(newHead) = intersection.material
        } else if (inDir > 0 && outDir > 0 && mediaHead > 0) {
          // if the ray is exiting a surface
          var mediaIndex = -1

          {
            var i = mediaHead
            while (i > 0) {
              if (media(i) == intersection.material) {
                mediaIndex = i
                i = 0
              }
              i -= 1
            }
          }
          if (-1 == mediaIndex) {
            // if the exited medium wasn't on the stack we just ignore it
            newMedia = media
            newHead = mediaHead
          } else if (mediaIndex == mediaHead) {
            // if the medium was the head we can just decrease the head index
            newMedia = media
            newHead = mediaHead - 1
          } else {
            // if the medium was somewhere in the middle, we need to build a new stack
            newMedia = new Array[Material](mediaHead)
            System.arraycopy(media, 0, newMedia, 0, mediaIndex)
            System.arraycopy(media, mediaIndex + 1, newMedia, mediaIndex, mediaHead - mediaIndex)
            newHead = mediaHead - 1
          }
        }
        val c = trace(newRay, scene, newMedia, newHead, i + 1, maxBounces, random)
        val cosine = Math.abs(surfaceNormal.dot(newDir)).toFloat
        color += c * bsdf * 2 * cosine
        d += 1
      }
      return absorbed * color / numSamples
    }
  }
}