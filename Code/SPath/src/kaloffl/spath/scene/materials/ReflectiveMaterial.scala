package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Vec2d
import kaloffl.spath.scene.SurfaceInfo

/**
 * @author Lars
 */
object ReflectiveMaterial {

  def apply(color: Color) =
    new Material(color, ReflectFunction)

  def apply(color: Color, glossiness: Double) =
    new Material(color, new GlossyReflectFunction(glossiness))
}