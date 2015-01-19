package kaloffl.spath.scene.shapes

import kaloffl.spath.math.Vec3f
import kaloffl.spath.tracing.Ray
import kaloffl.spath.math.Vec2f

trait Shape {

  def getNormal(point: Vec3f): Vec3f

  def getIntersectionDepth(ray: Ray): Float
}