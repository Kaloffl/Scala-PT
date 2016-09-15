package kaloffl.spath.scene

import kaloffl.spath.math.Vec3d
import kaloffl.spath.sampler.Sampler
import kaloffl.spath.scene.shapes.Closed

/**
  * With LightHints the creator of a scene can tell the renderer where it
  * should try to shoot rays.
  * A hint can for example be placed at the position of a lightsource to
  * guide the renderer to it. However these hints are not strictly coupled
  * to lights and can also be used to help the renderer find openings,
  * refractive objects and lots of other things.
  * Not all hints are usefull everywhere in the scene, so their influence
  * can be limited with the 'applicableFor' function to save resources.
  */
trait LightHint {
  def applicableFor(point: Vec3d): Boolean
  def sampler: Sampler
}

case class GlobalHint(override val sampler: Sampler) extends LightHint {
  override def applicableFor(point: Vec3d) = true
}
case class LocalHint(source: Closed, override val sampler: Sampler) extends LightHint {
  override def applicableFor(point: Vec3d) = source.contains(point)
}
case class ExclusionHint(excluded: Closed, override val sampler: Sampler) extends LightHint {
  override def applicableFor(point: Vec3d) = !excluded.contains(point)
}
