package kaloffl.spath.math

/**
 * A 3 dimensional mathematical vector of ddouble precision floating point
 * numbers. It implements the usual operations like addition and scaling but
 * also reflection, refraction and generation of random normal vectors centered
 * around the original vector.
 * These vectors are immutable and each operation creates a new instance with
 * the new values.
 *
 * @author Lars Donner
 */
case class Vec3d(x: Double, y: Double, z: Double) {

  def +(v: Vec3d): Vec3d = Vec3d(x + v.x, y + v.y, z + v.z)
  def +(f: Double): Vec3d = Vec3d(x + f, y + f, z + f)

  def -(v: Vec3d): Vec3d = Vec3d(x - v.x, y - v.y, z - v.z)
  def -(f: Double): Vec3d = Vec3d(x - f, y - f, z - f)

  def *(v: Vec3d): Vec3d = Vec3d(x * v.x, y * v.y, z * v.z)
  def *(f: Double): Vec3d = Vec3d(x * f, y * f, z * f)

  def /(v: Vec3d): Vec3d = Vec3d(x / v.x, y / v.y, z / v.z)
  def /(f: Double): Vec3d = Vec3d(x / f, y / f, z / f)

  def unary_-(): Vec3d = Vec3d(-x, -y, -z)

  def dot(v: Vec3d): Double = x * v.x + y * v.y + z * v.z

  def sum(): Double = x + y + z
  def lengthSq: Double = x * x + y * y + z * z
  def length: Double = Math.sqrt(x * x + y * y + z * z)

  def cross(v: Vec3d): Vec3d = {
    Vec3d(
      y * v.z - v.y * z,
      z * v.x - v.z * x,
      x * v.y - v.x * y)
  }

  def reflect(v: Vec3d): Vec3d = {
    val a = (x * v.x + y * v.y + z * v.z) * 2.0
    Vec3d(
      x - (v.x * a),
      y - (v.y * a),
      z - (v.z * a))
  }

  def normalize: Vec3d = {
    val len = Math.sqrt(x * x + y * y + z * z)
    Vec3d(x / len, y / len, z / len)
  }

  def refract(v: Vec3d, i1: Double, i2: Double): Vec3d = {
    val NdotI = dot(v)

    val eta = if (NdotI > 0.0) i2 / i1 else i1 / i2
    val k = eta * eta * (1.0 - NdotI * NdotI)

    if (k > 1.0f) {
      val a = (x * v.x + y * v.y + z * v.z) * 2.0f
      return Vec3d(
        x - (v.x * a),
        y - (v.y * a),
        z - (v.z * a))
    }

    val a = eta * NdotI - Math.sqrt(1.0 - k)
    val nx = x * eta + v.x * a
    val ny = y * eta + v.y * a
    val nz = z * eta + v.z * a
    val length = Math.sqrt(nx * nx + ny * ny + nz * nz)
    return Vec3d(nx / length, ny / length, nz / length)
  }

  def randomHemisphere(random: () ⇒ Float): Vec3d = {
    val angle = random() * 2.0 * Math.PI
    val distSq = random()
    val dist = Math.sqrt(distSq)

    // components of a normalized vector on the surface on a hemisphere where z >= 0
    val nx = dist * Math.cos(angle) // random number from -1 to 1
    val ny = dist * Math.sin(angle) // random number from -1 to 1
    val nz = Math.sqrt(1.0 - distSq) // random number from 0 to 1

    // plotting of nx, ny and nz values where x and y are the two random values:
    // http://www.wolframalpha.com/input/?i=plot%28sqrt%28y%29*cos%28x*Pi*2%29%2C+sqrt%28y%29*sin%28x*Pi*2%29%2C+sqrt%281+-+y%29%2C+x+%3D+0+to+1%2C+y+%3D+0+to+1%29

    // http://math.stackexchange.com/questions/61547/rotation-of-a-vector-distribution-to-align-with-a-normal-vector
    // the matrix rotation doesn't work near (0, 0, -1), so for that case we fall back on a simpler but slower method
    if (z < 0.0) {
      if (x * nx + y * ny + z * nz < 0.0) return Vec3d(-nx, -ny, -nz)
      return Vec3d(nx, ny, nz)
    }
    return ((Mat3d(
      z, 0, x,
      0, z, y,
      -x, -y, z) + Mat3d(
        y * y, -x * y, 0,
        -x * y, x * x, 0,
        0, 0, 0) * (1.0 / (1.0 + z))) * Vec3d(nx, ny, nz))
  }
}

/**
 * Useful standard vectors are predefined here.
 */
object Vec3d {
  val ORIGIN: Vec3d = Vec3d(0, 0, 0)
  val UNIT: Vec3d = Vec3d(1, 1, 1)
  val NEGATIVE: Vec3d = Vec3d(-1, -1, -1)

  val NORMAL = UNIT.normalize

  val LEFT: Vec3d = Vec3d(1, 0, 0)
  val RIGHT: Vec3d = Vec3d(-1, 0, 0)
  val UP: Vec3d = Vec3d(0, 1, 0)
  val DOWN: Vec3d = Vec3d(0, -1, 0)
  val FRONT: Vec3d = Vec3d(0, 0, 1)
  val BACK: Vec3d = Vec3d(0, 0, -1)

  val RED: Vec3d = LEFT
  val GREEN: Vec3d = UP
  val BLUE: Vec3d = FRONT
  val BLACK: Vec3d = ORIGIN
  val WHITE: Vec3d = UNIT
}