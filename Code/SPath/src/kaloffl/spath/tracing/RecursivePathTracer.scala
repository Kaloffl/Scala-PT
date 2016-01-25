package kaloffl.spath.tracing

import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.math.Color
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.math.Ray
import java.util.function.DoubleSupplier

class RecursivePathTracer(val scene: Scene) extends Tracer {

  override def trace(x: Float,
                     y: Float,
                     maxBounces: Int,
                     random: DoubleSupplier): Color = {
    val startRay = scene.camera.createRay(random, x, y)
    val mediaStack = scene.initialMediaStack
    val mediaHead = mediaStack.length - 1
    return trace(startRay, mediaStack, mediaHead, maxBounces, random)
  }

  def trace(ray: Ray, media: Array[Material], mediaHead: Int, i: Int, random: DoubleSupplier): Color = {
    if (0 == i) return Color.Black

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
            (media(mediaHead).getAbsorbtion(point, random) * -dist.toFloat).exp
          }
        return emitted * absorbed
      }

      val point = ray.atDistance(scatterDist)
      val absorbed = (media(mediaHead).getAbsorbtion(point, random) * -scatterDist.toFloat).exp
      val newRay = new Ray(point, Vec3d.randomNormal(Vec2d.random(random)))
      return absorbed * trace(newRay, media, mediaHead, i - 1, random)
    } else {
      val depth = intersection.depth
      val point = ray.atDistance(depth)
      val surfaceNormal = intersection.normal()
      val info = intersection.material.getInfo(
        incomingNormal = ray.normal,
        worldPos = point,
        surfaceNormal = surfaceNormal,
        textureCoordinate = intersection.textureCoordinate(),
        airRefractiveIndex = media(mediaHead).refractiveIndex,
        random = random)
      val absorbed = (media(mediaHead).getAbsorbtion(point, random) * -depth.toFloat).exp

      if (info.emittance != Color.Black) {
        return info.emittance * absorbed
      }

      val scattering = info.scattering
      val paths = scattering.paths
      var color = Color.Black
      var d = 0
      while (d < paths) {
        val weight = scattering.getWeight(d)
        if (weight > 0.0001) {
          val newDir = scattering.getNormal(d)
          val newRay = new Ray(point, newDir)
          if (newDir.dot(surfaceNormal) < 0) {
            // if the new ray has entered a surface
            val newHead = mediaHead + 1
            val newMedia = new Array[Material](newHead + 1)
            System.arraycopy(media, 0, newMedia, 0, newHead)
            newMedia(newHead) = intersection.material
            color += trace(newRay, newMedia, mediaHead + 1, i - 1, random) * weight
          } else if (ray.normal.dot(surfaceNormal) >= 0 && mediaHead > 0) {
            // if the ray is exiting a surface
            color += trace(newRay, media, mediaHead - 1, i - 1, random) * weight
          } else {
            color += trace(newRay, media, mediaHead, i - 1, random) * weight
          }
        }
        d += 1
      }
      return info.reflectance * absorbed * color
    }
  }
}