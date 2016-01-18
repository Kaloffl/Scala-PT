package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Attenuation
import kaloffl.spath.scene.materials.Scattering

class SurfaceInfo(
  val reflectance: Color,
  val emittance: Color,
  val scattering: Scattering)