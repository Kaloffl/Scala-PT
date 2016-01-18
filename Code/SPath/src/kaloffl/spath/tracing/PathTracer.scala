package kaloffl.spath.tracing

import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.math.Color
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.math.Ray

class PathTracer(val scene: Scene) extends Tracer {

  override def trace(x: Float,
                     y: Float,
                     maxBounces: Int,
                     context: Context): Color = {
    var color = Color.White
    var ray = scene.camera.createRay(context.random, x, y)
    var i = 0

    // list of materials in which the rays entered
    var mediaIndex = scene.initialMediaStack.length - 1
    val media = new Array[Material](maxBounces + 1)
    System.arraycopy(scene.initialMediaStack, 0, media, 0, scene.initialMediaStack.length)

    while (i < maxBounces) {
      // russian roulett ray termination
      val survivability = Math.max(color.r2, Math.max(color.g2, color.b2)) * (maxBounces - i)
      if (survivability < 1) {
        if (context.random.getAsDouble > survivability) {
          return Color.Black
        } else {
          color /= survivability
        }
      }

      // First we determine the distance it will take the ray to hit an air 
      // particle and be scattered. If the current air has a scatter probability 
      // of 0, the distance will be infinity.
      val scatterChance = context.random.getAsDouble
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
          val emitted = scene.skyMaterial.getEmittance(point, -ray.normal, ray.normal, context)
          val absorbed =
            if (java.lang.Double.isInfinite(dist)) {
              Color.White
            } else {
              (media(mediaIndex).getAbsorbtion(point, context) * -dist.toFloat).exp
            }
          return color * emitted * absorbed
        }

        val point = ray.atDistance(scatterDist)
        val absorbed = (media(mediaIndex).getAbsorbtion(point, context) * -scatterDist.toFloat).exp
        ray = new Ray(point, Vec3d.randomNormal(Vec2d.random(context.random)))
        color *= absorbed
      } else {
        val depth = intersection.depth
        val point = ray.atDistance(depth)
        val surfaceNormal = intersection.shape.getNormal(point)
        val info = intersection.material.getInfo(
          incomingNormal = ray.normal,
          worldPos = point,
          surfaceNormal = surfaceNormal,
          textureCoordinate = intersection.shape.getTextureCoordinate(point),
          airRefractiveIndex = media(mediaIndex).refractiveIndex,
          context = context)
        val absorbed = (media(mediaIndex).getAbsorbtion(point, context) * -depth.toFloat).exp

        if (info.emittance != Color.Black) {
          return color * info.emittance * absorbed
        }

        val scattering = info.scattering
        val rnd = context.random.getAsDouble
        var d = 0
        var weights = scattering.getWeight(d)
        while (weights < rnd) {
          d += 1
          weights += scattering.getWeight(d)
        }
        val newDir = scattering.getNormal(d)

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
    return Color.Black
  }
}