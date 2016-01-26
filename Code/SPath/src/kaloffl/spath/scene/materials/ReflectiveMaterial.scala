package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color

/**
 * @author Lars
 */
object ReflectiveMaterial {

  def apply(color: Color) =
    new Material(color, ReflectFunction)

  def apply(color: Color, glossiness: Float) =
    new Material(color, new GlossyReflectFunction(glossiness))
}