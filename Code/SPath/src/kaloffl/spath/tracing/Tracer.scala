package kaloffl.spath.tracing

import kaloffl.spath.math.Color

trait Tracer {

  def trace(ray: Ray, maxBounces: Int): Color
}