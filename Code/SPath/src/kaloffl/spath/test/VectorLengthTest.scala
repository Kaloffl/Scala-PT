package kaloffl.spath.test

import kaloffl.spath.math.Vec3d
import java.util.function.DoubleSupplier
import java.util.concurrent.ThreadLocalRandom
import kaloffl.spath.math.Vec2d

object VectorLengthTest {

  def main(args: Array[String]): Unit = {
    val rng = new DoubleSupplier() {
      override def getAsDouble() : Double = {
        ThreadLocalRandom.current().nextDouble()
      }
    }
    
    for(i <- 0 until 100) {
      val vec = Vec3d.UP.weightedHemisphere(Vec2d.random(rng))
      val len = vec.length
      if(len < 0.99 || len > 1.01) {
        println(len)
      }
    }
  }
}