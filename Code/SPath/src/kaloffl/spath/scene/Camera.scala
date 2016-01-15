package kaloffl.spath.scene

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Ray
import kaloffl.spath.math.Vec3d

/**
 * A camera that has a position and orientation in space and can be used to
 * create rays for the path tracing. It can also create a depth of field effect
 * if the aperture is a value greater than 1.
 */
class Camera(
    val position: Vec3d,
    val forward: Vec3d,
    val up: Vec3d,
    val aperture: Double = 0.0,
    val focalLength: Double = 10) {

  val right = forward.cross(up).normalize

  /**
   * Creates a Ray object starting on the "lens" and pointing into a direction
   * derived from the requested x and y position on the lens.
   *
   * @param random - a source of random numbers used for anti-aliasing and DOF
   * @param x - the horizontal position on the lens for the requested Ray
   * @param y - the vertical position on the lens for the requested Ray
   */
  def createRay(random: DoubleSupplier, x: Double, y: Double): Ray = {
    // This method is called for every pixel for every sample which is a lot.
    // Because of that all the vector calculations here were inlined by hand
    // to avoid a lot of object creation.

    // Calculate the point on the "lens" the ray is requested for, relative to
    // the camera position.
    val fX = right.x * x + up.x * y + forward.x
    val fY = right.y * x + up.y * y + forward.y
    val fZ = right.z * x + up.z * y + forward.z
    val fLength = Math.sqrt(fX * fX + fY * fY + fZ * fZ).toFloat

    // The random amount the actual ray will be offset by.
    // With a bigger aperture the offset can get bigger.
    val angle = random.getAsDouble * 2.0 * Math.PI
    val dist = Math.sqrt(1.0 - random.getAsDouble) * aperture
    val poX = dist * Math.cos(angle) + x
    val poY = dist * Math.sin(angle) + y

    // Calculate the position in space where the ray will start at.
    val pX = right.x * poX + up.x * poY + forward.x
    val pY = right.y * poX + up.y * poY + forward.y
    val pZ = right.z * poX + up.z * poY + forward.z
    val rayStart = Vec3d(pX + position.x, pY + position.y, pZ + position.z)

    // The position of the focus point for the requested pixel relative to the 
    // ray starting point on the lens.
    val len = focalLength - fLength
    val fpX = fX * len - pX
    val fpY = fY * len - pY
    val fpZ = fZ * len - pZ
    // Normalizing to make it correct for the Ray direction.
    val fpLength = Math.sqrt(fpX * fpX + fpY * fpY + fpZ * fpZ).toFloat
    val direction = Vec3d(fpX / fpLength, fpY / fpLength, fpZ / fpLength)

    return new Ray(rayStart, direction)
  }
}