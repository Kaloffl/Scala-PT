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

  /**
   * Calculates a Vector with the smallest x, y and z values of both vectors.
   * If one of the two vectors has all three of the smallest values it will
   * be returned and no new instance needs to be created.
   */
  def min(v: Vec3d): Vec3d = {
    if (x <= v.x && y <= v.y && z <= v.z) {
      return this
    }
    if (x > v.x && y > v.y && z > v.z) {
      return v
    }
    return Vec3d(Math.min(x, v.x), Math.min(y, v.y), Math.min(z, v.z))
  }

  /**
   * Calculates a Vector with the largest x, y and z values of both vectors.
   * If one of the two vectors has all three of the largest values it will
   * be returned and no new instance needs to be created.
   */
  def max(v: Vec3d): Vec3d = {
    if (x >= v.x && y >= v.y && z >= v.z) {
      return this
    }
    if (x < v.x && y < v.y && z < v.z) {
      return v
    }
    return Vec3d(Math.max(x, v.x), Math.max(y, v.y), Math.max(z, v.z))
  }

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
  }

  def ortho: Vec3d = {
    //  See : http://lolengine.net/blog/2013/09/21/picking-orthogonal-vector-combing-coconuts
    return if (Math.abs(x) > Math.abs(z)) Vec3d(-y, x, 0) else Vec3d(0, -z, y);
  }

  def randomConeSample(random: Vec2d, extent: Double, bias: Double): Vec3d = {
    val baseX = ortho.normalize
    val baseY = cross(baseX).normalize
    val angle = random.x * 2 * Math.PI
    val rnd = Math.pow(1.0 - random.y * extent, 1.0 / (bias + 1.0))
    val dist = Math.sqrt(1.0 - rnd * rnd)
    return baseX * (Math.cos(angle) * dist) +
      baseY * (Math.sin(angle) * dist) +
      this * rnd
  }

  def randomHemisphere(random: Vec2d, bias: Double): Vec3d = {
    return randomConeSample(random, 1.0, bias)
  }

  def randomHemisphere(random: Vec2d): Vec3d = {
    return randomConeSample(random, 1.0, 0.0)
  }

  def weightedHemisphere(random: Vec2d): Vec3d = {
    return randomConeSample(random, 1.0, 1.0)
  }
}

/**
 * Useful standard vectors are predefined here.
 */
object Vec3d {
  val Origin: Vec3d = Vec3d(0, 0, 0)
  val Unit: Vec3d = Vec3d(1, 1, 1)
  val Negative: Vec3d = Vec3d(-1, -1, -1)

  val Normal = Unit.normalize

  val Left: Vec3d = Vec3d(1, 0, 0)
  val Right: Vec3d = Vec3d(-1, 0, 0)
  val Up: Vec3d = Vec3d(0, 1, 0)
  val Down: Vec3d = Vec3d(0, -1, 0)
  val Front: Vec3d = Vec3d(0, 0, 1)
  val Back: Vec3d = Vec3d(0, 0, -1)

  val DIRECTIONS = Array(Left, Right, Up, Down, Front, Back)

  def apply(): Vec3d = Origin
  def apply(d: Double): Vec3d = new Vec3d(d, d, d)

  def randomNormal(random: Vec2d): Vec3d = {
    val angle = random.x * 2.0 * Math.PI
    val rnd = random.y * 2 - 1
    val distSq = 1.0 - rnd * rnd
    val dist = Math.sqrt(distSq)

    val nx = dist * Math.cos(angle)
    val ny = dist * Math.sin(angle)
    val nz = rnd
    return Vec3d(nx, ny, nz)
  }
}