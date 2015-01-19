package kaloffl.spath.math

case class Mat3f(
    m11: Float, m12: Float, m13: Float,
    m21: Float, m22: Float, m23: Float,
    m31: Float, m32: Float, m33: Float) {

  def +(m: Mat3f): Mat3f = {
    Mat3f(
      m11 + m.m11, m12 + m.m12, m13 + m.m13,
      m21 + m.m21, m22 + m.m22, m23 + m.m23,
      m31 + m.m31, m32 + m.m32, m33 + m.m33)
  }

  def *(f: Float): Mat3f = {
    Mat3f(
      m11 * f, m12 * f, m13 * f,
      m21 * f, m22 * f, m23 * f,
      m31 * f, m32 * f, m33 * f)
  }

  def *(v: Vec3f): Vec3f = {
    Vec3f(
      m11 * v.x + m12 * v.y + m13 * v.z,
      m21 * v.x + m22 * v.y + m23 * v.z,
      m31 * v.x + m32 * v.y + m33 * v.z)
  }

  def *(m: Mat3f): Mat3f = {
    Mat3f(
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

  def transposition: Mat3f = {
    Mat3f(
      m11, m21, m31,
      m12, m22, m32,
      m13, m23, m33)
  }
}