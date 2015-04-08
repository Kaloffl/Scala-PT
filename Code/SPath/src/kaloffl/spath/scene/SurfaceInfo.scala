package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Attenuation

class SurfaceInfo(
  val reflectance: Color,
  val emittance: Color,
  val attenuation: Attenuation,
  val outgoing: Vec3d,
  val translucent: Boolean)