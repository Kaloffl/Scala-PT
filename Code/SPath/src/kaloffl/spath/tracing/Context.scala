package kaloffl.spath.tracing

import kaloffl.spath.Display
import java.util.function.DoubleSupplier

class Context(
  val random: DoubleSupplier,
  val passNum: Int,
  val maxBounces: Int,
  val display: Display)