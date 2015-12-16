package kaloffl.spath.tracing

import kaloffl.spath.math.Color
import kaloffl.spath.scene.materials.Material

trait Tracer {

  def trace(ray: Ray, maxBounces: Int, air: Material, context: Context): Color
}