package kaloffl.spath.math

import java.util.function.DoubleSupplier

/**
 * A 3 dimensional mathematical vector of double precision floating point
 * numbers. It implements the usual operations like addition and scaling but
 * also reflection, refraction and generation of random normal vectors centered
 * around the original vector.
 * These vectors are immutable and each operation creates a new instance with
 * the new values.
 */
case class Vec3d(val x: Double, val y: Double, val z: Double) {

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

  def min = Math.min(x, Math.min(y, z))
  def max = Math.max(x, Math.max(y, z))
  def abs = Vec3d(Math.abs(x), Math.abs(y), Math.abs(z))

  def pow(v: Vec3d): Vec3d = {
    Vec3d(Math.pow(x, v.x), Math.pow(y, v.y), Math.pow(z, v.z))
  }
  def pow(f: Double): Vec3d = {
    Vec3d(Math.pow(x, f), Math.pow(y, f), Math.pow(z, f))
  }

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

  def refractance(v: Vec3d, i1: Double, i2: Double): Double = {
    val cosI = -dot(v)
    val n = i1 / i2
    val sinT2 = n * n * (1.0 - cosI * cosI)
    if (sinT2 > 1.0) {
      return 1.0
    }
    val cosT = Math.sqrt(1.0 - sinT2)
    val rOrth = (i1 * cosI - i2 * cosT) / (i1 * cosI + i2 * cosT)
    val rPar = (i2 * cosI - i1 * cosT) / (i2 * cosI + i1 * cosT)
    return (rOrth * rOrth + rPar * rPar) / 2.0
  }

  def refract(v: Vec3d, i1: Double, i2: Double): Vec3d = {
    val cosI = -dot(v)
    val n = i1 / i2
    val sinT2 = n * n * (1.0 - cosI * cosI)

    if (sinT2 > 1.0) {
      val a = cosI * -2.0f
      return Vec3d(
        x - (v.x * a),
        y - (v.y * a),
        z - (v.z * a))
    }

    val a = n * cosI - Math.sqrt(1.0 - sinT2)
    val nx = x * n + v.x * a
    val ny = y * n + v.y * a
    val nz = z * n + v.z * a
    return Vec3d(nx, ny, nz)
    //    val length = Math.sqrt(nx * nx + ny * ny + nz * nz)
    //    return Vec3d(nx / length, ny / length, nz / length)
  }

  def randomHemisphere(random: DoubleSupplier): Vec3d = {
    val angle = random.getAsDouble * 2.0 * Math.PI
    val rnd = random.getAsDouble
    val distSq = 1.0 - rnd * rnd
    val dist = Math.sqrt(distSq)

    // components of a normalized vector on the surface of a hemisphere 
    // where z > 0
    val nx = dist * Math.cos(angle)
    val ny = dist * Math.sin(angle)
    val nz = rnd

    // if the angle between the original and the new vector is more than 90°, 
    // we just turn it 180°
    if (x * nx + y * ny + z * nz < 0.0) return Vec3d(-nx, -ny, -nz)
    return Vec3d(nx, ny, nz)
  }

  def weightedHemisphere(random: DoubleSupplier): Vec3d = {
    val angle = random.getAsDouble * 2.0 * Math.PI
    val rnd = random.getAsDouble
    val distSq = (1 - rnd * rnd)
    val dist = Math.sqrt(rnd)

    val nx = dist * Math.cos(angle)
    val ny = dist * Math.sin(angle)
    val nz = Math.sqrt(1 - dist * dist)

    if (z < 0) {
      return ((Mat3d(
        -z, 0, x,
        0, -z, y,
        -x, -y, -z) + Mat3d(
          y * y, -x * y, 0,
          -x * y, x * x, 0,
          0, 0, 0) * (1.0f / (1.0f - z))) * Vec3d(-nx, -ny, -nz))
    }
    return ((Mat3d(
      z, 0, x,
      0, z, y,
      -x, -y, z) + Mat3d(
        y * y, -x * y, 0,
        -x * y, x * x, 0,
        0, 0, 0) * (1.0f / (1.0f + z))) * Vec3d(nx, ny, nz))
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

  def apply(): Vec3d = BLACK
  def apply(d: Double): Vec3d = new Vec3d(d, d, d)

  def randomNormal(random: DoubleSupplier): Vec3d = {
    val angle = random.getAsDouble * 2.0 * Math.PI
    val rnd = random.getAsDouble * 2 - 1
    val distSq = 1.0 - rnd * rnd
    val dist = Math.sqrt(distSq)

    val nx = dist * Math.cos(angle)
    val ny = dist * Math.sin(angle)
    val nz = rnd
    return Vec3d(nx, ny, nz)
  }
}