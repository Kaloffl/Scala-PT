package kaloffl.spath.tracing

import kaloffl.spath.math.{Vec2d, Vec3d}
import kaloffl.spath.scene.materials.Material

class Intersection(
    val depth: Double,
    val material: Material,
    val normal: () ⇒ Vec3d,
    val textureCoordinate: () ⇒ Vec2d) {

  def hitObject = java.lang.Double.isFinite(depth)
}

object Intersection {
  val NullIntersection = new Intersection(
    depth = Double.PositiveInfinity,
    material = null,
    normal = null,
    textureCoordinate = null)
}