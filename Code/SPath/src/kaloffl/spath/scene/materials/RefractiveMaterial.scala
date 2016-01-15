package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Vec2d
import kaloffl.spath.scene.SurfaceInfo

object RefractiveMaterial {

  def apply(color: Color,
            refractivityIndex: Double,
            glossiness: Double = 0) =
    new Material(
      color,
      new RefractFunction(refractivityIndex, glossiness))

}