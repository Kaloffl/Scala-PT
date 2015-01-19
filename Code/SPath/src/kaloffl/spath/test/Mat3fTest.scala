package kaloffl.spath.test

import org.junit.Test
import kaloffl.spath.math.Mat3f
import org.junit.Assert
import kaloffl.spath.math.Vec3f

class Mat3fTest {

  val identity = Mat3f(1, 0, 0, 0, 1, 0, 0, 0, 1)

  @Test
  def addTest1: Unit = {
    val mat1 = Mat3f(1, 1, 1, 1, 1, 1, 1, 1, 1)
    val mat2 = Mat3f(2, 2, 2, 2, 2, 2, 2, 2, 2)
    Assert.assertEquals(mat2, mat1 + mat1)
  }

  @Test
  def multTest1: Unit = {
    val mat1 = Mat3f(1, 1, 1, 1, 1, 1, 1, 1, 1)
    val mat2 = Mat3f(3, 3, 3, 3, 3, 3, 3, 3, 3)
    Assert.assertEquals(mat2, mat1 * mat1)
  }

  @Test
  def multTest2: Unit = {
    val mat1 = Mat3f(1, 2, 3, 4, 5, 6, 7, 8, 9)
    Assert.assertEquals(mat1, mat1 * identity)
    Assert.assertEquals(mat1, identity * mat1)
  }

  @Test
  def multTest3: Unit = {
    val mat = Mat3f(1, 1, -1, 0, 1, 1, 2, 1, 0)
    val vec1 = Vec3f(1, -1, 2)
    val vec2 = Vec3f(-2, 1, 1)
    Assert.assertEquals(vec2, mat * vec1)
  }

  @Test
  def multTest4: Unit = {
    val mat1 = Mat3f(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val scala = 2
    val mat2 = Mat3f(2, 4, 6, 8, 10, 12, 14, 16, 18)
    Assert.assertEquals(mat2, mat1 * scala)
  }

  @Test
  def multTest5: Unit = {
    val mat1 = Mat3f(-2, 3, 1, 6, -9, -3, 4, -6, -2)
    val mat2 = Mat3f(3, 1, 1, 2, 0, 1, 0, 2, -1)
    val mat3 = Mat3f(0, 0, 0, 0, 0, 0, 0, 0, 0)
    Assert.assertEquals(mat3, mat1 * mat2)
    val mat4 = Mat3f(4, -6, -2, 0, 0, 0, 8, -12, -4)
    Assert.assertEquals(mat4, mat2 * mat1)
  }
}