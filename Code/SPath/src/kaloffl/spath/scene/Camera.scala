package kaloffl.spath.scene

import kaloffl.spath.math.Vec3f
import kaloffl.spath.tracing.Ray

class Camera(
    val position: Vec3f,
    val forward: Vec3f,
    val up: Vec3f,
    val aperture: Float,
    val focalLength: Float) {

  val right = forward.cross(up).normalize

  def createRay(random: () ⇒ Float, x: Int, y: Int, w: Int, h: Int): Ray = {

    // offset of the pixel we want to create the ray for
    // with a little randomness for cheap anti aliasing
    val foX = (x - w * 0.5f + random() * 2.0f - 1.0f) / h
    val foY = (h * 0.5f - y + random() * 2.0f - 1.0f) / h

    // the position of said pixel
    val fX = right.x * foX + up.x * foY + forward.x
    val fY = right.y * foX + up.y * foY + forward.y
    val fZ = right.z * foX + up.z * foY + forward.z
    val fLength = Math.sqrt(fX * fX + fY * fY + fZ * fZ).toFloat

    // the offset the ray will start at. With a bigger aperture it
    // can lay further away from the pixel we want to render.
    val poX = foX + (random() * 2.0f - 1.0f) * aperture / h
    val poY = foY + (random() * 2.0f - 1.0f) * aperture / h

    // the position where the ray starts at
    val pX = right.x * poX + up.x * poY + forward.x
    val pY = right.y * poX + up.y * poY + forward.y
    val pZ = right.z * poX + up.z * poY + forward.z
    val pixelPosition = Vec3f(pX + position.x, pY + position.y, pZ + position.z)

    // the position of the focus point of this pixel relative to the 
    // ray starting point
    val len = focalLength / fLength
    val fpX = fX * len - pX
    val fpY = fY * len - pY
    val fpZ = fZ * len - pZ
    val fpLength = Math.sqrt(fpX * fpX + fpY * fpY + fpZ * fpZ).toFloat
    val direction = Vec3f(fpX / fpLength, fpY / fpLength, fpZ / fpLength)

    return new Ray(pixelPosition, direction)
  }
}