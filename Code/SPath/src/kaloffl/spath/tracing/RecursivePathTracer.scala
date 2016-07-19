package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Ray, Vec2d, Vec3d}
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material

object RecursivePathTracer extends Tracer {

  override def trace(ray: Ray,
    scene: Scene,
    maxBounces: Int,
    random: DoubleSupplier): (Color, Int) = {
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
    random: DoubleSupplier): (Color, Int) = {
    if (i == maxBounces) return (Color.Black, 1)

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
            return (emitted, 1)
          } else {
            return (emitted * (media(mediaHead).absorbtion * -dist.toFloat).exp, 1)
          }
      }

      val point = ray.atDistance(scatterDist)
      val absorbed = (media(mediaHead).absorbtion * -scatterDist.toFloat).exp
      val randomRay = new Ray(point, Vec3d.randomNormal(Vec2d.random(random)))
      var (color, paths) = trace(randomRay, scene, media, mediaHead, i + 1, maxBounces, random)
      if (i > 1) {
        var h = 0
        val hints = scene.lightHints
        while (h < hints.length) {
          val hint = hints(h)
          if (hint.applicableFor(point)) {
            val lightRay = hint.target.createRandomRay(point, random)
            val angle = hint.target.getSolidAngle(point).toFloat
            val (c, p) = trace(lightRay, scene, media, mediaHead, i * 2, maxBounces, random)
            color += c * angle
            paths += p
          }
          h += 1
        }
      }
      return (absorbed * color, paths)
    } else {
      val depth = intersection.depth
      val absorbed = if (media(mediaHead).absorbtion != Color.Black) {
        (media(mediaHead).absorbtion * -depth.toFloat).exp
      } else {
        Color.White
      }

      if (intersection.material.emission != Color.Black) {
        return (intersection.material.emission * absorbed, 1)
      }

      val point = ray.atDistance(depth)
      val surfaceNormal = intersection.normal()
      val scatterings = intersection.material.getScattering(
        incomingNormal = ray.normal,
        surfaceNormal = surfaceNormal,
        uv = intersection.textureCoordinate(),
        outsideIor = media(mediaHead).ior,
        random = random)

      var color = Color.Black
      var evaluatedPaths = 0
      var d = 0
      val minWeight = i.toFloat / maxBounces
      while (d < scatterings.length) {
        val s = scatterings(d)
        val weight = s.weight
        if (weight > minWeight) {
          val newDir = s.normal
          val newRay = new Ray(point, newDir)
          if (newDir.dot(surfaceNormal) < 0) {
            // if the new ray has entered a surface
            val newHead = mediaHead + 1
            val newMedia = new Array[Material](newHead + 1)
            System.arraycopy(media, 0, newMedia, 0, newHead)
            newMedia(newHead) = intersection.material
            val (c, p) = trace(newRay, scene, newMedia, mediaHead + 1, i + 1, maxBounces, random)
            color += c * weight * s.color
            evaluatedPaths += p
          } else if (ray.normal.dot(surfaceNormal) >= 0 && mediaHead > 0) {
            // if the ray is exiting a surface
            val (c, p) = trace(newRay, scene, media, mediaHead - 1, i + 1, maxBounces, random)
            color += c * weight * s.color
            evaluatedPaths += p
          } else {
            val (c, p) = trace(newRay, scene, media, mediaHead, i + 1, maxBounces, random)
          	 color += c * weight * s.color
          	 evaluatedPaths += p
          }
        }
        d += 1
      }
      return (absorbed * color, evaluatedPaths)
    }
  }
}