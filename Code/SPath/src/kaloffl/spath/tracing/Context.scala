package kaloffl.spath.tracing

import kaloffl.spath.Display
import java.util.function.DoubleSupplier
import kaloffl.spath.RenderTarget

class Context(
  val random: DoubleSupplier,
  val passNum: Int,
  val maxBounces: Int,
  val display: RenderTarget)