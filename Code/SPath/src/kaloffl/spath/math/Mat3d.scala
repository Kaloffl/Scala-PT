package kaloffl.spath.math

/**
 * A 3 by 3 matrix of double precision floating point numbers for doing math
 * operations.
 *
 * @author Lars Donner
 */
case class Mat3d(
    m11: Double, m12: Double, m13: Double,
    m21: Double, m22: Double, m23: Double,
    m31: Double, m32: Double, m33: Double) {

  def +(m: Mat3d): Mat3d = {
    Mat3d(
      m11 + m.m11, m12 + m.m12, m13 + m.m13,
      m21 + m.m21, m22 + m.m22, m23 + m.m23,
      m31 + m.m31, m32 + m.m32, m33 + m.m33)
  }

  def *(f: Double): Mat3d = {
    Mat3d(
      m11 * f, m12 * f, m13 * f,
      m21 * f, m22 * f, m23 * f,
      m31 * f, m32 * f, m33 * f)
  }

  def *(v: Vec3d): Vec3d = {
    Vec3d(
      m11 * v.x + m12 * v.y + m13 * v.z,
      m21 * v.x + m22 * v.y + m23 * v.z,
      m31 * v.x + m32 * v.y + m33 * v.z)
  }

  def *(m: Mat3d): Mat3d = {
    Mat3d(
      m11 * m.m11 + m12 * m.m21 + m13 * m.m31,
      m11 * m.m12 + m12 * m.m22 + m13 * m.m32,
      m11 * m.m13 + m12 * m.m23 + m13 * m.m33,

      m21 * m.m11 + m22 * m.m21 + m23 * m.m31,
      m21 * m.m12 + m22 * m.m22 + m23 * m.m32,
      m21 * m.m13 + m22 * m.m23 + m23 * m.m33,

      m31 * m.m11 + m32 * m.m21 + m33 * m.m31,
      m31 * m.m12 + m32 * m.m22 + m33 * m.m32,
      m31 * m.m13 + m32 * m.m23 + m33 * m.m33)
  }

  def transposition: Mat3d = {
    Mat3d(
      m11, m21, m31,
      m12, m22, m32,
      m13, m23, m33)
  }
}