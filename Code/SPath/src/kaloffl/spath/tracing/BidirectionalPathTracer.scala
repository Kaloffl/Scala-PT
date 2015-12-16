package kaloffl.spath.tracing

import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.math.Vec2d
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Color
import kaloffl.spath.scene.Scene

class BidirectionalPathTracer(scene: Scene) extends Tracer {

  // TODO remove first camera ray
  override def trace(ray: Ray, maxBounces: Int, startAir: Material, context: Context): Color = {
        val lightLength = maxBounces / 2 + 1
    val lightPoints = new Array[Vec3d](lightLength)
    val lightNormals = new Array[Vec3d](lightLength)
    val lightMaterials = new Array[Material](lightLength)
    val lightRay = scene.getRandomLightRay(context.random)
    if (null == lightRay) {
      System.err.println("asdfasdfasdf")
    }
    //    val lightAir = startAir
    val lightColor = {
      val start = lightRay.start + lightRay.normal * 0.001
      val intersection = scene.getIntersection(new Ray(start, -lightRay.normal), 0.002)
      if (null == intersection) {
        System.err.println("noooooo")
      }
      val pos = start
      lightMaterials(0) = intersection.material
      intersection.material.getEmittance(pos, intersection.shape.getNormal(pos), -lightRay.normal, 0.001, context)
    }
    lightPoints(0) = lightRay.start
    lightNormals(0) = lightRay.normal

    val camLength = maxBounces / 2 + 1
    val camPoints = new Array[Vec3d](camLength)
    val camNormals = new Array[Vec3d](camLength)
    val camMaterials = new Array[Material](camLength)
    val camRay = ray
    val camColor = Color.WHITE
    camPoints(0) = camRay.start
    camNormals(0) = camRay.normal

    pathTraceA(lightRay, lightColor, scene.air, lightPoints, lightNormals, lightMaterials, context)
    pathTraceA(camRay, camColor, scene.air, camPoints, camNormals, camMaterials, context)

    var color = Color.BLACK
    var paths = 0
    var i = 1
    var camPathColor = Color.WHITE
    while (i < camPoints.length) {
      if (null == camMaterials(i)) {
        i = camPoints.length
      } else {
        val incoming = camPoints(i) - camPoints(i - 1)
        val length = incoming.length
        val ni = incoming / length
        val info = camMaterials(i).getInfo(camPoints(i), camNormals(i), ni, length, 1.0, context)
        camPathColor *= info.reflectance
        val diffuse = if ((i + 1) < camPoints.length && null != camPoints(i + 1)) {
          camNormals(i).dot((camPoints(i + 1) - camPoints(i)).normalize).toFloat
        } else {
          1.0f
        }
        if (!camMaterials(i).isInstanceOf[DiffuseMaterial]) {
          val emittance = info.emittance
          if (emittance.r2 > 0 || emittance.g2 > 0 || emittance.b2 > 0) {
            paths += 1
            camPathColor *= emittance * diffuse * 2
            color += camPathColor
            i = camPoints.length
          }
        } else {
          var j = 0
          while (j < lightPoints.length) {
            if (null == lightMaterials(j)) {
              j = lightPoints.length
            } else if (0 != j && !lightMaterials(j).isInstanceOf[DiffuseMaterial]) {
              // skip if the light ray hit another light source
            } else if (0 == j && (i + 1) < camMaterials.length && camMaterials(i + 1) == lightMaterials(0)) {
              // skip connecting the rays if the cam ray will hit the light next bounce anyways
            } else {
              val diff = lightPoints(j) - camPoints(i)
              val length = diff.length
              val dir = diff.normalize
              val ray = new Ray(camPoints(i), dir)
              val intersect = scene.getIntersection(ray, length + 0.001)
              if (null != intersect && Math.abs(intersect.depth - length) < 0.002) {
                var fullPathColor = camPathColor * camNormals(i).dot(dir).toFloat * 2
                var k = j
                while (k >= 0) {
                  val incoming = if (k == j) {
                    diff
                  } else {
                    lightPoints(k) - lightPoints(k + 1)
                  }
                  val length = incoming.length
                  val ni = incoming / length
                  val info = lightMaterials(k).getInfo(lightPoints(k), lightNormals(k), ni, length, 1.0, context)
                  if (0 == k) {
                    val attenuation = lightMaterials(0).asInstanceOf[LightMaterial].attenuation
                    color += fullPathColor * info.emittance * attenuation(length)
                  } else {
                    val diffuse = lightNormals(k).dot((lightPoints(k - 1) - lightPoints(k)).normalize).toFloat
                    fullPathColor *= info.reflectance * diffuse * 2
                  }
                  k -= 1
                }
              }
              paths += 1
            }
            j += 1
          }
          camPathColor *= diffuse * 2
        }
      }
      i += 1
    }

    if (0 == paths) return Color.BLACK
    return color / (paths * scene.lights.length)
  }

  def pathTraceA(
    startRay: Ray,
    startColor: Color,
    startAir: Material,
    points: Array[Vec3d],
    normals: Array[Vec3d],
    materials: Array[Material],
    context: Context): Unit = {
    var ray = startRay
    var air = startAir
    var color = startColor
    var i = 1
    while (i < points.length) {
      // russian roulett ray termination
      val survivability = Math.min(1, Math.max(color.r2, Math.max(color.g2, color.b2)) * (points.length - i))
      if (context.random.getAsDouble > survivability) {
        return
      } else {
        color /= survivability
      }

      val scatterChance = context.random.getAsDouble
      val scatterDist = Math.log(scatterChance + 1) / air.scatterPropability

      val intersection = scene.getIntersection(ray, scatterDist)
      if (null == intersection) {
        if (java.lang.Double.isInfinite(scatterDist)) {
          // TODO handle sky
          return
        }

        val point = ray.start + ray.normal * scatterDist
        val absorbed = (air.getAbsorbtion(point, context)
          * (air.absorbtionCoefficient * -scatterDist).toFloat).exp
        ray = new Ray(point, Vec3d.randomNormal(Vec2d.random(context.random)))
        color *= absorbed
        points(i) = point
        materials(i) = air
      } else {
        val depth = intersection.depth
        materials(i) = intersection.material
        val point = ray.normal * depth + ray.start
        points(i) = point
        val surfaceNormal = intersection.shape.getNormal(point)
        normals(i) = surfaceNormal
        val info = intersection.material.getInfo(point, surfaceNormal, ray.normal, depth, air.refractivityIndex, context)
        val absorbed = (air.getAbsorbtion(point, context) * (air.absorbtionCoefficient * -depth).toFloat).exp

        if (info.emittance != Color.BLACK) {
          return
        }

        val newDir = info.outgoing
        if (newDir.dot(surfaceNormal) < 0) {
          air = intersection.material
        } else {
          air = scene.air
        }
        ray = new Ray(point, newDir)
        color *= info.reflectance * absorbed
      }
      i += 1
    }
  }
}