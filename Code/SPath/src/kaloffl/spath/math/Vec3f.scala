package kaloffl.spath.math

case class Vec3f(x: Float, y: Float, z: Float) {

  def +(v: Vec3f): Vec3f = Vec3f(x + v.x, y + v.y, z + v.z)
  def +(f: Float): Vec3f = Vec3f(x + f, y + f, z + f)

  def -(v: Vec3f): Vec3f = Vec3f(x - v.x, y - v.y, z - v.z)
  def -(f: Float): Vec3f = Vec3f(x - f, y - f, z - f)

  def *(v: Vec3f): Vec3f = Vec3f(x * v.x, y * v.y, z * v.z)
  def *(f: Float): Vec3f = Vec3f(x * f, y * f, z * f)

  def /(v: Vec3f): Vec3f = Vec3f(x / v.x, y / v.y, z / v.z)
  def /(f: Float): Vec3f = Vec3f(x / f, y / f, z / f)

  def unary_-(): Vec3f = Vec3f(-x, -y, -z)

  def dot(v: Vec3f): Float = x * v.x + y * v.y + z * v.z

  def sum(): Float = x + y + z
  def lengthSq: Float = x * x + y * y + z * z
  def length: Float = Math.sqrt(x * x + y * y + z * z).toFloat

  def cross(v: Vec3f): Vec3f = {
    Vec3f(
      y * v.z - v.y * z,
      z * v.x - v.z * x,
      x * v.y - v.x * y)
  }

  def reflect(v: Vec3f): Vec3f = {
    val a = (x * v.x + y * v.y + z * v.z) * 2.0f
    Vec3f(
      x - (v.x * a),
      y - (v.y * a),
      z - (v.z * a))
  }

  def normalize: Vec3f = {
    val len = Math.sqrt(x * x + y * y + z * z).toFloat
    Vec3f(x / len, y / len, z / len)
  }

  def quickNormalize: Vec3f = {
    val length = Math.sqrt(x * x + y * y + z * z).toFloat
    Vec3f(x / length, y / length, z / length)
  }

  def refract(v: Vec3f, i1: Float, i2: Float): Vec3f = {
    val NdotI = dot(v)

    val eta = if (NdotI > 0.0f) i2 / i1 else i1 / i2
    val k = eta * eta * (1.0f - NdotI * NdotI)

    if (k > 1.0f) {
      val a = (x * v.x + y * v.y + z * v.z) * 2.0f
      return Vec3f(
        x - (v.x * a),
        y - (v.y * a),
        z - (v.z * a))
    }

    val a = eta * NdotI - Math.sqrt(1.0f - k).toFloat
    val nx = x * eta + v.x * a
    val ny = y * eta + v.y * a
    val nz = z * eta + v.z * a
    val length = Math.sqrt(nx * nx + ny * ny + nz * nz).toFloat
    return Vec3f(nx / length, ny / length, nz / length)
  }

  def randomHemisphere(random: () ⇒ Float): Vec3f = {
    val angle = random() * Vec3f.FLOAT_PI2
    val distSq = random()
    val dist = Math.sqrt(distSq).toFloat

    // components of a normalized vector on the surface on a henisphere where z >= 0
    val nx = dist * Math.cos(angle).toFloat // random number from -1 to 1
    val ny = dist * Math.sin(angle).toFloat // random number from -1 to 1
    val nz = Math.sqrt(1.0f - distSq).toFloat // random number from 0 to 1

    // plotting of nx, ny and nz values where x and y are the two random values:
    // http://www.wolframalpha.com/input/?i=plot%28sqrt%28y%29*cos%28x*Pi*2%29%2C+sqrt%28y%29*sin%28x*Pi*2%29%2C+sqrt%281+-+y%29%2C+x+%3D+0+to+1%2C+y+%3D+0+to+1%29

    // http://math.stackexchange.com/questions/61547/rotation-of-a-vector-distribution-to-align-with-a-normal-vector
    // the matrix rotation doesn't work near (0, 0, -1), so for that case we fall back on a simpler but slower method
    if (z < 0.0f) {
      if (x * nx + y * ny + z * nz < 0.0f) return Vec3f(-nx, -ny, -nz)
      return Vec3f(nx, ny, nz)
    }
    return ((Mat3f(
      z, 0, x,
      0, z, y,
      -x, -y, z) + Mat3f(
        y * y, -x * y, 0,
        -x * y, x * x, 0,
        0, 0, 0) * (1.0f / (1.0f + z))) * Vec3f(nx, ny, nz))
  }
}

object Vec3f {
  val FLOAT_PI2: Float = (Math.PI * 2.0).toFloat;

  val ORIGIN: Vec3f = Vec3f(0, 0, 0)
  val UNIT: Vec3f = Vec3f(1, 1, 1)
  val NEGATIVE = Vec3f(-1, -1, -1)

  val NORMAL = UNIT.normalize

  val LEFT: Vec3f = Vec3f(1, 0, 0)
  val RIGHT: Vec3f = Vec3f(-1, 0, 0)
  val UP: Vec3f = Vec3f(0, 1, 0)
  val DOWN: Vec3f = Vec3f(0, -1, 0)
  val FRONT: Vec3f = Vec3f(0, 0, 1)
  val BACK: Vec3f = Vec3f(0, 0, -1)

  val RED: Vec3f = LEFT
  val GREEN: Vec3f = UP
  val BLUE: Vec3f = FRONT
  val BLACK: Vec3f = ORIGIN
  val WHITE: Vec3f = UNIT
}