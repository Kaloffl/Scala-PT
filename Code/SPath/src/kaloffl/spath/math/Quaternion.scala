package kaloffl.spath.math

case class Quaternion(r: Double, i: Double, j: Double, k: Double) {

  def *(q: Quaternion) = Quaternion(
    r * q.r - i * q.i - j * q.j - k * q.k,
    r * q.i + i * q.r + j * q.k - k * q.j,
    r * q.j - i * q.k + j * q.r + k * q.i,
    r * q.k + i * q.j - j * q.i + k * q.r)

  def *(v: Vec3d) = Quaternion(
    -i * v.x - j * v.y - k * v.z,
    r * v.x + j * v.z - k * v.y,
    r * v.y - i * v.z + k * v.x,
    r * v.z + i * v.y - j * v.x)

  def unary_~ = Quaternion(r, -i, -j, -k)
  
  def toVec3 = Vec3d(i, j, k)
}

object Quaternion {

  def apply(v: Vec3d, theta: Double): Quaternion = {
    val thetaOver2 = theta / 2
    val tsin = Math.sin(thetaOver2)
    Quaternion(Math.cos(thetaOver2), v.x * tsin, v.y * tsin, v.z * tsin)
  }

  def apply(v: Vec3d): Quaternion = Quaternion(0, v.x, v.y, v.z)
}