package kaloffl.spath.scene

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Ray
import kaloffl.spath.math.Vec3d

/**
 * A camera that has a position and orientation in space and can be used to
 * create rays for the path tracing.
 */
trait Camera {
  def createRay(random: DoubleSupplier, x: Float, y: Float): Ray
}

class PinholeCamera(val position: Vec3d,
    val forward: Vec3d,
    val up: Vec3d,
    val sensorWidth: Float = 0.01f,
    val sensorHeight: Float = 0.01f,
    val sensorDistance: Float = 0.005f) extends Camera {

  val right = forward.cross(up).normalize

  override def createRay(random: DoubleSupplier, x: Float, y: Float): Ray = {
    val sx = sensorWidth / 2 * x
    val sy = sensorHeight / 2 * y
    val dX = right.x * sx + up.x * sy + forward.x * sensorDistance
    val dY = right.y * sx + up.y * sy + forward.y * sensorDistance
    val dZ = right.z * sx + up.z * sy + forward.z * sensorDistance
    val dL = Math.sqrt(dX * dX + dY * dY + dZ * dZ)
    val direction = Vec3d(dX / dL, dY / dL, dZ / dL)

    return new Ray(position, direction)
  }
}

class LensCamera(val position: Vec3d, 
    val forward: Vec3d, 
    val up: Vec3d, 
    val sensorWidth: Float = 0.01f,
    val sensorHeight: Float = 0.01f,
    val sensorDistance: Float = 0.005f,
    val lensRadius: Float = 0, 
    val focussedDepth: Float = 1) extends Camera {
  
  val right = (forward cross up).normalize
  
  override def createRay(random: DoubleSupplier, x: Float, y: Float): Ray = {
    val angle = random.getAsDouble * 2.0 * Math.PI
    val dist = Math.sqrt(random.getAsDouble) * lensRadius
    val poX = dist * Math.cos(angle)
    val poY = dist * Math.sin(angle)
    
    val lX = right.x * poX + up.x * poY
    val lY = right.y * poX + up.y * poY
    val lZ = right.z * poX + up.z * poY
    val rayStart = Vec3d(lX + position.x, lY + position.y, lZ + position.z)
    
    val sx = sensorWidth / 2 * x
    val sy = sensorHeight / 2 * y
    val dX = right.x * sx + up.x * sy + forward.x * sensorDistance
    val dY = right.y * sx + up.y * sy + forward.y * sensorDistance
    val dZ = right.z * sx + up.z * sy + forward.z * sensorDistance
    val dL = focussedDepth / Math.sqrt(dX * dX + dY * dY + dZ * dZ)
    val fX = dX * dL - lX
    val fY = dY * dL - lY
    val fZ = dZ * dL - lZ
    val fL = Math.sqrt(fX * fX + fY * fY + fZ * fZ)
    val rayDirection = Vec3d(fX / fL, fY / fL, fZ / fL)
    
    return new Ray(rayStart, rayDirection)
  }
}