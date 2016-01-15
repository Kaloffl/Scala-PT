package kaloffl.spath.tracing

import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.math.Vec3d

class Intersection(
    val depth: Double,
    val material: Material,
    val shape: Shape) {

  def hitObject = (null != shape)
}

object Intersection {
  val NullIntersection = new Intersection(
    depth = Double.PositiveInfinity,
    material = null,
    shape = null)
}