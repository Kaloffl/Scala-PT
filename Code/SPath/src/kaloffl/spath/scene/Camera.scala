package kaloffl.spath.scene

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Ray, Vec3d}

/**
 * A camera that has a position and orientation in space and can be used to
 * create rays for the path tracing.
 */
trait Camera {
  def createRay(view: Viewpoint, random: DoubleSupplier, x: Float, y: Float): Ray
}

class PinholeCamera(
    val sensorWidth: Float = 0.01f,
    val sensorHeight: Float = 0.01f,
    val sensorDistance: Float = 0.005f) extends Camera {

  override def createRay(view: Viewpoint, random: DoubleSupplier, x: Float, y: Float): Ray = {
    val sx = sensorWidth / 2 * x
    val sy = sensorHeight / 2 * y
    val dX = view.right.x * sx + view.up.x * sy + view.forward.x * sensorDistance
    val dY = view.right.y * sx + view.up.y * sy + view.forward.y * sensorDistance
    val dZ = view.right.z * sx + view.up.z * sy + view.forward.z * sensorDistance
    val dL = Math.sqrt(dX * dX + dY * dY + dZ * dZ)
    val direction = Vec3d(dX / dL, dY / dL, dZ / dL)

    return new Ray(view.position, direction)
  }
}

class LensCamera(
    val sensorWidth: Float = 0.01f,
    val sensorHeight: Float = 0.01f,
    val sensorDistance: Float = 0.005f,
    val lensRadius: Float = 0, 
    val focussedDepth: Float = 1) extends Camera {
  
  override def createRay(view: Viewpoint, random: DoubleSupplier, x: Float, y: Float): Ray = {
    val angle = random.getAsDouble * 2.0 * Math.PI
    val dist = Math.sqrt(random.getAsDouble) * lensRadius
    val poX = dist * Math.cos(angle)
    val poY = dist * Math.sin(angle)
    
    val lX = view.right.x * poX + view.up.x * poY
    val lY = view.right.y * poX + view.up.y * poY
    val lZ = view.right.z * poX + view.up.z * poY
    val rayStart = Vec3d(lX + view.position.x, lY + view.position.y, lZ + view.position.z)
    
    val sx = sensorWidth / 2 * x
    val sy = sensorHeight / 2 * y
    val dX = view.right.x * sx + view.up.x * sy + view.forward.x * sensorDistance
    val dY = view.right.y * sx + view.up.y * sy + view.forward.y * sensorDistance
    val dZ = view.right.z * sx + view.up.z * sy + view.forward.z * sensorDistance
    val dL = focussedDepth / Math.sqrt(dX * dX + dY * dY + dZ * dZ)
    val fX = dX * dL - lX
    val fY = dY * dL - lY
    val fZ = dZ * dL - lZ
    val fL = Math.sqrt(fX * fX + fY * fY + fZ * fZ)
    val rayDirection = Vec3d(fX / fL, fY / fL, fZ / fL)
    
    return new Ray(rayStart, rayDirection)
  }
}