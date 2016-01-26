package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color

object RefractiveMaterial {

  def apply(color: Color, refractiveIndex: Float, glossiness: Float = 0) =
    new Material(color, new RefractFunction(refractiveIndex, glossiness))

}