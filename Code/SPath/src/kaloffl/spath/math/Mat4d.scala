package kaloffl.spath.math

case class Mat4d(
    m11: Double, m12: Double, m13: Double, m14: Double,
    m21: Double, m22: Double, m23: Double, m24: Double,
    m31: Double, m32: Double, m33: Double, m34: Double,
    m41: Double, m42: Double, m43: Double, m44: Double) {

  def +(m: Mat4d): Mat4d = {
    Mat4d(
      m11 + m.m11, m12 + m.m12, m13 + m.m13, m14 + m.m14,
      m21 + m.m21, m22 + m.m22, m23 + m.m23, m24 + m.m24,
      m31 + m.m31, m32 + m.m32, m33 + m.m33, m34 + m.m34,
      m41 + m.m41, m42 + m.m42, m43 + m.m43, m44 + m.m44)
  }

  def *(f: Double): Mat4d = {
    Mat4d(
      m11 * f, m12 * f, m13 * f, m14 * f,
      m21 * f, m22 * f, m23 * f, m24 * f,
      m31 * f, m32 * f, m33 * f, m34 * f,
      m41 * f, m42 * f, m43 * f, m44 * f)
  }

  def *(v: Vec4d): Vec4d = {
    Vec4d(
      m11 * v.x + m12 * v.y + m13 * v.z + m14 * v.w,
      m21 * v.x + m22 * v.y + m23 * v.z + m24 * v.w,
      m31 * v.x + m32 * v.y + m33 * v.z + m34 * v.w,
      m41 * v.x + m42 * v.y + m43 * v.z + m44 * v.w)
  }

  def *(m: Mat4d): Mat4d = {
    Mat4d(
      m11 * m.m11 + m12 * m.m21 + m13 * m.m31 + m14 * m.m41,
      m11 * m.m12 + m12 * m.m22 + m13 * m.m32 + m14 * m.m42,
      m11 * m.m13 + m12 * m.m23 + m13 * m.m33 + m14 * m.m43,
      m11 * m.m14 + m12 * m.m24 + m13 * m.m34 + m14 * m.m44,

      m21 * m.m11 + m22 * m.m21 + m23 * m.m31 + m24 * m.m41,
      m21 * m.m12 + m22 * m.m22 + m23 * m.m32 + m24 * m.m42,
      m21 * m.m13 + m22 * m.m23 + m23 * m.m33 + m24 * m.m43,
      m21 * m.m14 + m22 * m.m24 + m23 * m.m34 + m24 * m.m44,

      m31 * m.m11 + m32 * m.m21 + m33 * m.m31 + m34 * m.m41,
      m31 * m.m12 + m32 * m.m22 + m33 * m.m32 + m34 * m.m42,
      m31 * m.m13 + m32 * m.m23 + m33 * m.m33 + m34 * m.m43,
      m31 * m.m14 + m32 * m.m24 + m33 * m.m34 + m34 * m.m44,

      m41 * m.m11 + m42 * m.m21 + m43 * m.m31 + m44 * m.m41,
      m41 * m.m12 + m42 * m.m22 + m43 * m.m32 + m44 * m.m42,
      m41 * m.m13 + m42 * m.m23 + m43 * m.m33 + m44 * m.m43,
      m41 * m.m14 + m42 * m.m24 + m43 * m.m34 + m44 * m.m44)
  }

  def transposition: Mat4d = {
    Mat4d(
      m11, m21, m31, m41,
      m12, m22, m32, m42,
      m13, m23, m33, m43,
      m14, m24, m34, m44)
  }

  def inverse: Mat4d = {
    val inv = Mat4d(
      m11 = m22 * m33 * m44 -
        m22 * m34 * m43 -
        m32 * m23 * m44 +
        m32 * m24 * m43 +
        m42 * m23 * m34 -
        m42 * m24 * m33,

      m12 = -m12 * m33 * m44 +
        m12 * m34 * m43 +
        m32 * m13 * m44 -
        m32 * m14 * m43 -
        m42 * m13 * m34 +
        m42 * m14 * m33,

      m13 = m12 * m23 * m44 -
        m12 * m24 * m43 -
        m22 * m13 * m44 +
        m22 * m14 * m43 +
        m42 * m13 * m24 -
        m42 * m14 * m23,

      m14 = -m12 * m23 * m34 +
        m12 * m24 * m33 +
        m22 * m13 * m34 -
        m22 * m14 * m33 -
        m32 * m13 * m24 +
        m32 * m14 * m23,

      m21 = -m21 * m33 * m44 +
        m21 * m34 * m43 +
        m31 * m23 * m44 -
        m31 * m24 * m43 -
        m41 * m23 * m34 +
        m41 * m24 * m33,

      m22 = m11 * m33 * m44 -
        m11 * m34 * m43 -
        m31 * m13 * m44 +
        m31 * m14 * m43 +
        m41 * m13 * m34 -
        m41 * m14 * m33,

      m23 = -m11 * m23 * m44 +
        m11 * m24 * m43 +
        m21 * m13 * m44 -
        m21 * m14 * m43 -
        m41 * m13 * m24 +
        m41 * m14 * m23,

      m24 = m11 * m23 * m34 -
        m11 * m24 * m33 -
        m21 * m13 * m34 +
        m21 * m14 * m33 +
        m31 * m13 * m24 -
        m31 * m14 * m23,

      m31 = m21 * m32 * m44 -
        m21 * m34 * m42 -
        m31 * m22 * m44 +
        m31 * m24 * m42 +
        m41 * m22 * m34 -
        m41 * m24 * m32,

      m32 = -m11 * m32 * m44 +
        m11 * m34 * m42 +
        m31 * m12 * m44 -
        m31 * m14 * m42 -
        m41 * m12 * m34 +
        m41 * m14 * m32,

      m33 = m11 * m22 * m44 -
        m11 * m24 * m42 -
        m21 * m12 * m44 +
        m21 * m14 * m42 +
        m41 * m12 * m24 -
        m41 * m14 * m22,

      m34 = -m11 * m22 * m34 +
        m11 * m24 * m32 +
        m21 * m12 * m34 -
        m21 * m14 * m32 -
        m31 * m12 * m24 +
        m31 * m14 * m22,

      m41 = -m21 * m32 * m43 +
        m21 * m33 * m42 +
        m31 * m22 * m43 -
        m31 * m23 * m42 -
        m41 * m22 * m33 +
        m41 * m23 * m32,

      m42 = m11 * m32 * m43 -
        m11 * m33 * m42 -
        m31 * m12 * m43 +
        m31 * m13 * m42 +
        m41 * m12 * m33 -
        m41 * m13 * m32,

      m43 = -m11 * m22 * m43 +
        m11 * m23 * m42 +
        m21 * m12 * m43 -
        m21 * m13 * m42 -
        m41 * m12 * m23 +
        m41 * m13 * m22,

      m44 = m11 * m22 * m33 -
        m11 * m23 * m32 -
        m21 * m12 * m33 +
        m21 * m13 * m32 +
        m31 * m12 * m23 -
        m31 * m13 * m22)

    val det = m11 * inv.m11 + m12 * inv.m21 + m13 * inv.m31 + m14 * inv.m41
//    if(det == 0) throw new RuntimeException(s"det = $det inv = $inv")
    return inv * (1 / det)
  }
}