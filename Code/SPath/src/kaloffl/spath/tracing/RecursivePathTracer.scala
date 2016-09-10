package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Ray, Vec2d, Vec3d}
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.Shape

class RecursivePathTracer(maxBounces: Int) extends Tracer {

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
        val point = ray.atDistance(dist)
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
      var angleSum = 0f
      var direct = Color.Black
      if (i < maxBounces / 2) {
        var h = 0
        val hints = scene.lightHints
        while (h < hints.length) {
          val hint = hints(h)
          if (hint.applicableFor(point)) {
            val lightRay = hint.target.createRandomRay(point, random)
            val angle = hint.target.getSolidAngle(point).toFloat
            angleSum += angle
            val c = trace(lightRay, scene, media, mediaHead, i + 1, maxBounces / 2, random)
            direct += c * angle
          }
          h += 1
        }
      }
      return absorbed * (indirect * (1 - angleSum) + direct)
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
      val uv = intersection.textureCoordinate()
      val (scatterings, weights) = intersection.material.getScattering(
        incomingNormal = ray.normal,
        surfaceNormal = surfaceNormal,
        uv = uv,
        outsideIor = media(mediaHead).ior,
        random = random)

      var color = Color.Black
      var d = 0
      val minWeight = i.toFloat / maxBounces
      while (d < scatterings.length) {
        val newDir = scatterings(d)
        val weight = weights(d)
        val bsdf = intersection.material.evaluateBSDF(-ray.normal, surfaceNormal, newDir, uv, media(mediaHead).ior)
        val survivalChance = 1.0f / Math.max(1, i - maxBounces / 4) * Math.min(weight * 2, 1)
        if (survivalChance > random.getAsDouble) {
          val newRay = new Ray(point, newDir)
          val inDir = ray.normal.dot(surfaceNormal)
          val outDir = newDir.dot(surfaceNormal)
          if (inDir < 0 && outDir < 0) {
            // if the new ray has entered a surface
            val newHead = mediaHead + 1
            val newMedia = new Array[Material](newHead + 1)
            System.arraycopy(media, 0, newMedia, 0, newHead)
            newMedia(newHead) = intersection.material
            val c = trace(newRay, scene, newMedia, mediaHead + 1, i + 1, maxBounces, random)
            color += c * bsdf * weight
          } else if (inDir > 0 && outDir > 0 && mediaHead > 0) {
            // if the ray is exiting a surface
            var mediaIndex = -1
            var i = mediaHead
            while (i > 0) {
              if (media(i) == intersection.material) {
                mediaIndex = i
                i = 0
              }
              i -= 1
            }
            if (-1 == i) {
              // if the exited medium wasn't on the stack we just ignore it
              val c = trace(newRay, scene, media, mediaHead, i + 1, maxBounces, random)
              color += c * bsdf * weight
            } else if (i == mediaHead) {
              // if the medium was the head we can just decrease the head index
              val c = trace(newRay, scene, media, mediaHead - 1, i + 1, maxBounces, random)
              color += c * bsdf * weight
            } else {
              // if the medium was somewhere in the middle, we need to build a new stack
              val newStack = new Array[Material](mediaHead)
              System.arraycopy(media, 0, newStack, 0, i)
              System.arraycopy(media, i + 1, newStack, i, mediaHead - i)
              val c = trace(newRay, scene, newStack, mediaHead - 1, i + 1, maxBounces, random)
              color += c * bsdf * weight
            }
          } else {
            var angleSum = 0f
            var direct = Color.Black
            if (i < maxBounces / 2) {
              val hints = scene.lightHints
              if (0 < hints.length) {
                val hint = hints((random.getAsDouble * hints.length).toInt)
                if (hint.applicableFor(point)) {
                  if (hint.target.asInstanceOf[Shape].getIntersectionDepth(newRay).isInfinite) {
                    val lightRay = hint.target.createRandomRay(point, random)
                    val lightAngle = surfaceNormal.dot(lightRay.normal)
                    if (0 < lightAngle) {
                      val bsdf = intersection.material.evaluateBSDF(
                        toEye = -ray.normal,
                        surfaceNormal = surfaceNormal,
                        toLight = lightRay.normal,
                        uv = uv,
                        outsideIor = media(mediaHead).ior)
                      if (bsdf != Color.Black) {
                        val angle = hint.target.getSolidAngle(point).toFloat
                        angleSum += angle
                        val c = trace(lightRay, scene, media, mediaHead, i + 1, maxBounces / 2, random)
                        direct += c * angle * bsdf * weight * Math.abs(lightAngle).toFloat
                      }
                    }
                  }
                }
              }
            }

            val indirect = if (angleSum < 0.99f) {
              trace(newRay, scene, media, mediaHead, i + 1, maxBounces, random) * bsdf * weight
            } else {
              Color.Black
            }

            color += (indirect * (1 - angleSum) + direct)
          }
          color /= survivalChance
        }
        d += 1
      }
      return absorbed * color
    }
  }
}