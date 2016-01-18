package kaloffl.spath.tracing

import kaloffl.spath.math.Color
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.math.Ray
import java.util.function.DoubleSupplier

trait Tracer {

  def trace(x: Float,
            y: Float,
            maxBounces: Int,
            random: DoubleSupplier): Color
}