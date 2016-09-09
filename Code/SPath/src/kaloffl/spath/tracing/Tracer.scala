package kaloffl.spath.tracing

import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Ray}
import kaloffl.spath.scene.Scene

trait Tracer {

  def trace(ray: Ray,
            scene: Scene,
            random: DoubleSupplier): Color
}