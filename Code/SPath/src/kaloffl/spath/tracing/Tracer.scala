package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Color
import kaloffl.spath.scene.Scene

trait Tracer {

  def trace(scene: Scene,
            x: Float,
            y: Float,
            maxBounces: Int,
            random: DoubleSupplier): Color
}