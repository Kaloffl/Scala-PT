package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Color
import kaloffl.spath.math.Ray
import kaloffl.spath.scene.Scene

trait Tracer {

  def trace(ray: Ray,
            scene: Scene,
            maxBounces: Int,
            random: DoubleSupplier): Color
}