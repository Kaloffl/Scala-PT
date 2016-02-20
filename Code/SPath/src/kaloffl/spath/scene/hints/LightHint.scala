package kaloffl.spath.scene.hints

import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.shapes.Projectable
import kaloffl.spath.scene.shapes.Closed

trait LightHint {
  def target: Projectable
  def applicableFor(point: Vec3d): Boolean
}

case class GlobalHint(override val target: Projectable) extends LightHint {
  override def applicableFor(point: Vec3d) = true
}
case class LocalHint(source: Closed, override val target: Projectable) extends LightHint{
    override def applicableFor(point: Vec3d) = source.contains(point)
}