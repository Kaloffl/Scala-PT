package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3f
import kaloffl.spath.tracing.Ray

trait EstimatedShape extends Shape {

  def estimateDepth(p: Vec3f): Float

  def getIntersectionDepth(ray: Ray): Float = {
    var depthSum = 0.0f
    var counter = 0
    while (counter < 20) {
      val pos = ray.start + ray.normal * depthSum
      var depth = estimateDepth(pos)
      depthSum += depth

      if (depth < 0.002) {
        return depthSum
      }
      counter += 1
    }
    return Float.PositiveInfinity
  }
}