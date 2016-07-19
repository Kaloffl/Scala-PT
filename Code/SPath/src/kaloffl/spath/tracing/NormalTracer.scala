package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Ray}
import kaloffl.spath.scene.Scene

object NormalTracer extends Tracer {

  override def trace(ray: Ray,
                     scene: Scene,
                     maxBounces: Int,
                     random: DoubleSupplier): (Color, Int) = {

    val intersection = scene.getIntersection(ray, Double.PositiveInfinity)
    if (!intersection.hitObject) {
      return (Color.Black, 1)
    } else {
      val depth = intersection.depth
      val surfaceNormal = intersection.normal()
      if(surfaceNormal.lengthSq > 1.1) {
        println(surfaceNormal)
        return (Color.Red, 1)
      }
      return (Color(surfaceNormal / 2 + 0.5), 1)
    }
  }
}