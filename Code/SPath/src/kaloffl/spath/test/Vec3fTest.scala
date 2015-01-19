package kaloffl.spath.test

import java.util.concurrent.ThreadLocalRandom

import kaloffl.spath.math.Vec3f

object Vec3fTest {

  def main(args: Array[String]): Unit = {
    val random: () ⇒ Float = ThreadLocalRandom.current.nextFloat

    case class Data(v: Vec3f, minLen: Float, maxLen: Float, minDot: Float, maxDot: Float)

    for (start ← Seq(Vec3f.LEFT, Vec3f.RIGHT, Vec3f.UP, Vec3f.DOWN, Vec3f.FRONT, Vec3f.BACK)) {

      val data = Data(start, 10.0f, -10.0f, 10.0f, -10.0f)
      val result = (0 until 1000).foldLeft(data) { (d, i) ⇒
        val next = d.v.randomHemisphere(random)
        val len = next.lengthSq
        val minLen = Math.min(d.minLen, len)
        val maxLen = Math.max(d.maxLen, len)
        val dot = d.v.dot(next)
        val minDot = Math.min(d.minDot, dot)
        val maxDot = Math.max(d.maxDot, dot)
        Data(next, minLen, maxLen, minDot, maxDot)
      }
      println(result)
    }
  }
}