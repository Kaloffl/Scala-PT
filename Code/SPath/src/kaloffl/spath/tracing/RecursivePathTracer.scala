package kaloffl.spath.tracing

import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.math.Color
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.math.Ray

class RecursivePathTracer(val scene: Scene) extends Tracer {

  override def trace(x: Float,
                     y: Float,
                     maxBounces: Int,
                     context: Context): Color = {
    val startRay = scene.camera.createRay(context.random, x, y)
    return trace(startRay, scene.initialMediaStack.toSeq, maxBounces, context)
  }

  def trace(ray: Ray, media: Seq[Material], i: Int, context: Context): Color = {
    if (0 == i) return Color.Black

    // First we determine the distance it will take the ray to hit an air 
    // particle and be scattered. If the current air has a scatter probability 
    // of 0, the distance will be infinity.
    val scatterChance = context.random.getAsDouble
    val scatterDist = -Math.log(1 - scatterChance) / media.head.scatterProbability

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
        val emitted = scene.skyMaterial.getEmittance(point, -ray.normal, ray.normal, context)
        val absorbed =
          if (java.lang.Double.isInfinite(dist)) {
            Color.White
          } else {
            (media.head.getAbsorbtion(point, context) * -dist.toFloat).exp
          }
        return emitted * absorbed
      }

      val point = ray.atDistance(scatterDist)
      val absorbed = (media.head.getAbsorbtion(point, context) * -scatterDist.toFloat).exp
      val newRay = new Ray(point, Vec3d.randomNormal(Vec2d.random(context.random)))
      return absorbed * trace(newRay, media, i - 1, context)
    } else {
      val depth = intersection.depth
      val point = ray.atDistance(depth)
      val surfaceNormal = intersection.shape.getNormal(point)
      val info = intersection.material.getInfo(
        incomingNormal = ray.normal,
        worldPos = point,
        surfaceNormal = surfaceNormal,
        textureCoordinate = intersection.shape.getTextureCoordinate(point),
        airRefractiveIndex = media.head.refractiveIndex,
        context = context)
      val absorbed = (media.head.getAbsorbtion(point, context) * -depth.toFloat).exp

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
          val newMedia =
            if (newDir.dot(surfaceNormal) < 0) {
              // if the new ray has entered a surface
              intersection.material +: media
            } else if (ray.normal.dot(surfaceNormal) >= 0 && media.size > 1) {
              // if the ray is exiting a surface
              media.tail
            } else {
              media
            }
          val newRay = new Ray(point, newDir)
          color += trace(newRay, newMedia, i - 1, context) * weight
        }
        d += 1
      }
      return info.reflectance * absorbed * color
    }
  }
}