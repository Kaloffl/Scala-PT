package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Ray}
import kaloffl.spath.scene.Scene

object TexcoordTracer extends Tracer {

  override def trace(ray: Ray,
                     scene: Scene,
                     maxBounces: Int,
                     random: DoubleSupplier): (Color, Int) = {

    val intersection = scene.getIntersection(ray, Double.PositiveInfinity)
    if (!intersection.hitObject) {
      return (Color.Black, 1)
    } else {
      val depth = intersection.depth
      val texcoord = intersection.textureCoordinate()
      return (Color(texcoord.x.toFloat, texcoord.y.toFloat, 0), 1)
    }
  }
}