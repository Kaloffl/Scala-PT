package kaloffl.spath.tracing
import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Ray, Vec2d, Vec3d}
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material

/**
  * Created by Lars on 02.09.2016.
  */
class BidirectionalPathTracer(
                               maxCameraBounces: Int,
                               maxLightBounces: Int
                             ) extends Tracer {

  override def trace(
                      ray: Ray,
                      scene: Scene,
                      random: DoubleSupplier): Color = {

    val cameraMediaStack = new Array[Material](maxCameraBounces + scene.initialMediaStack.length)
    for (i <- scene.initialMediaStack.indices) cameraMediaStack(i) = scene.initialMediaStack(i)
    var cameraMediaStackHead = scene.initialMediaStack.length - 1

    class PathVertex(
                 val toEye: Vec3d,
                 val pos: Vec3d,
                 val normal: Vec3d,
                 val uv: Vec2d,
                 val material: Material,
                 val importance: Color)
    class PathEdge(
                  val medium: Material,
                  val length: Double)

    val points = new Array[PathVertex](maxCameraBounces)
    val edges = new Array[PathEdge](maxCameraBounces)
    var importanceLength = 1
    var connections = 0

    {
      val firstIntersection = scene.getIntersection(ray, Double.MaxValue)
      if (!firstIntersection.hitObject) {
        return scene.skyMaterial.getEmittance(ray.normal)
      }
      if (firstIntersection.material.emission != Color.Black) {
        return firstIntersection.material.emission
      }
      edges(0) = new PathEdge(
        medium = cameraMediaStack(cameraMediaStackHead),
        length = firstIntersection.depth)

      points(0) = new PathVertex(
        toEye = -ray.normal,
        pos = ray.atDistance(firstIntersection.depth),
        normal = firstIntersection.normal(),
        uv = firstIntersection.textureCoordinate(),
        material = firstIntersection.material,
        importance = Color.White)
    }

    var color = Color.Black

    var continueGatherWalk = true
    while (importanceLength < maxCameraBounces && continueGatherWalk) {
      val lastPoint = points(importanceLength - 1)
      val (scatterings, weights) = lastPoint.material.getScattering(
        incomingNormal = -lastPoint.toEye,
        surfaceNormal = lastPoint.normal,
        uv = lastPoint.uv,
        outsideIor = cameraMediaStack(cameraMediaStackHead).ior,
        random = random)

      val newDir = {
        val rand = random.getAsDouble
        var i = 1
        var weightSum = weights(0)
        while(rand > weightSum && i < scatterings.length) {
          weightSum += weights(i)
          i += 1
        }
        scatterings(i - 1)
      }

      val importance = lastPoint.importance * lastPoint.material.evaluateBSDF(
        toEye = lastPoint.toEye,
        surfaceNormal = lastPoint.normal,
        toLight = newDir,
        uv = lastPoint.uv,
        outsideIor = cameraMediaStack(cameraMediaStackHead).ior)

      if (lastPoint.toEye.dot(lastPoint.normal) > 0 && lastPoint.normal.dot(newDir) < 0) {
        cameraMediaStackHead += 1
        cameraMediaStack(cameraMediaStackHead) = lastPoint.material
      } else if (lastPoint.toEye.dot(lastPoint.normal) < 0 && lastPoint.normal.dot(newDir) > 0) {
        // TODO fix the problem where this is triggered without actually exiting a medium
        cameraMediaStackHead = Math.max(0, cameraMediaStackHead - 1)
      }

      val ray = new Ray(lastPoint.pos, newDir)
      val intersection = scene.getIntersection(ray, Double.MaxValue)
      if (intersection.hitObject) {
        val material = intersection.material
        if (Color.Black == material.emission) {
          edges(importanceLength) = new PathEdge(
            medium = cameraMediaStack(cameraMediaStackHead),
            length = intersection.depth)

          points(importanceLength) = new PathVertex(
            toEye = -newDir,
            pos = ray.atDistance(intersection.depth),
            normal = intersection.normal(),
            uv = intersection.textureCoordinate(),
            material = material,
            importance = importance)
          importanceLength += 1
        } else {
          if (importanceLength < 3) {
            return material.emission * importance
          } else {
            color += material.emission * importance
            continueGatherWalk = false
            connections += 1
          }
        }
      } else {
        if (importanceLength < 3) {
          return scene.skyMaterial.getEmittance(ray.normal) * importance
        } else {
          color += scene.skyMaterial.getEmittance(ray.normal) * importance
          continueGatherWalk = false
          connections += 1
        }
      }
    }

    val lightSources = scene.lightSources
    var lightLength = 1

      //for ((lightShape, lightMaterial) <- lightSources) {
    if (0 < lightSources.length) {
      val (lightShape, lightMaterial) = lightSources((random.getAsDouble * lightSources.length).toInt)
      val isConnected = new Array[Boolean](importanceLength)
      val point = lightShape.getRandomPointOfSurface(random)
      val surfaceNormal = lightShape.getNormal(point)
      var lightColor = lightMaterial.emission
      var pendingConnections = importanceLength

      for(j <- 0 until importanceLength) {
        val p = points(j)
        val diff = p.pos - point
        val distance = diff.length
        val dir = diff / distance
        if (dir.dot(surfaceNormal) > 0) {
          val intersection = scene.getIntersection(new Ray(point, dir), distance + 0.001)
          if (Math.abs(intersection.depth - distance) < 0.001) {
            val bsdf = p.material.evaluateBSDF(
              toEye = p.toEye,
              surfaceNormal = p.normal,
              toLight = -dir,
              uv = p.uv,
              outsideIor = Color.White)
            if (bsdf != Color.Black) {
              pendingConnections -= 1
              isConnected(j) = true
              color += p.importance * bsdf * lightColor / Math.pow(intersection.depth, 2).toFloat
              connections += 1
            }
          }
        }
      }
      var lightRay = new Ray(point, surfaceNormal.randomHemisphere(Vec2d.random(random)))
      var continueLightWalk = true

      while (pendingConnections > 0 && lightLength < maxLightBounces && continueLightWalk) {
        val intersection = scene.getIntersection(lightRay, Double.MaxValue)
        if (intersection.hitObject && intersection.material.emission == Color.Black) {
          val surfaceNormal = intersection.normal()
          val uv = intersection.textureCoordinate()

          val point = lightRay.atDistance(intersection.depth)
          val (scatterings, weights) = intersection.material.getScattering(
            incomingNormal = lightRay.normal,
            surfaceNormal = surfaceNormal,
            uv = uv,
            outsideIor = Color.White,
            random = random)

          val newDir = {
            val rand = random.getAsDouble
            var i = 1
            var weightSum = weights(0)
            while (rand > weightSum && i < scatterings.length) {
              weightSum += weights(i)
              i += 1
            }
            scatterings(i - 1)
          }

          val bsdf = intersection.material.evaluateBSDF(
            toEye = newDir,
            surfaceNormal = surfaceNormal,
            toLight = -lightRay.normal,
            uv = uv,
            outsideIor = Color.White)

          lightRay = new Ray(point, newDir)

          for(j <- 0 until importanceLength) {
            if (!isConnected(j)) {
              val p = points(j)
              val diff = p.pos - point
              val distance = diff.length
              val dir = diff / distance
              if (dir.dot(surfaceNormal) > 0) {
                val intersection = scene.getIntersection(new Ray(point, dir), distance + 0.001)
                if (Math.abs(intersection.depth - distance) < 0.001) {
                  val bsdf = p.material.evaluateBSDF(
                    toEye = p.toEye,
                    surfaceNormal = p.normal,
                    toLight = -dir,
                    uv = p.uv,
                    outsideIor = Color.White)
                  if (bsdf != Color.Black) {
                    if (!isConnected(j)) {
                      pendingConnections -= 1
                      isConnected(j) = true
                    }
                    color += p.importance * bsdf * lightColor / Math.pow(intersection.depth, 2).toFloat
                    connections += 1
                  }
                }
              }
            }
          }
          lightColor *= bsdf
          lightLength += 1
        } else {
          continueLightWalk = false
          // light ray didn't hit any scenery. too bad
        }
      }
    }
    if (0 == connections) return Color.Black
    //return Color.White / connections
    return color / connections
  }
}
