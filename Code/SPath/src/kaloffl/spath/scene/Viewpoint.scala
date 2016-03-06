package kaloffl.spath.scene

import kaloffl.spath.math.Vec3d

class Viewpoint(val position: Vec3d, val forward: Vec3d, val up: Vec3d) {
  
  val right = forward.cross(up)
}