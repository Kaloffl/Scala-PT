package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color

object DiffuseMaterial {
  def apply(color: Color) = new Material(color, DiffuseFunction)
}