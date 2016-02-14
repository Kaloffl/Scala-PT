package kaloffl.spath.scene

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Ray
import kaloffl.spath.math.Vec3d

/**
 * A camera that has a position and orientation in space and can be used to
 * create rays for the path tracing. It can also create a depth of field effect
 * if the aperture is a value greater than 0.
 */
class Camera(
    val position: Vec3d,
    val forward: Vec3d,
    val up: Vec3d,
    val aperture: Float = 0,
    val focalLength: Float = 1) {

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

    // Calculate the point on the sensor the ray is requested for
    val fX = -right.x * x - up.x * y - forward.x * focalLength
    val fY = -right.y * x - up.y * y - forward.y * focalLength
    val fZ = -right.z * x - up.z * y - forward.z * focalLength
    val rayStart = Vec3d(fX + position.x, fY + position.y, fZ + position.z)

    // The random amount the ray will be offset by.
    // With a bigger aperture the offset will get bigger.
    val angle = random.getAsDouble * 2.0 * Math.PI
    val dist = Math.sqrt(random.getAsDouble) * aperture
    val poX = dist * Math.cos(angle)
    val poY = dist * Math.sin(angle)
    
    val dX = right.x * poX + up.x * poY - fX
    val dY = right.y * poX + up.y * poY - fY
    val dZ = right.z * poX + up.z * poY - fZ
    val dL = Math.sqrt(dX * dX + dY * dY + dZ * dZ)
    val direction = Vec3d(dX / dL, dY / dL, dZ / dL)

    return new Ray(rayStart, direction)
  }
}