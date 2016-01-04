package kaloffl.spath.scene

import java.util.function.DoubleSupplier
import kaloffl.spath.bvh.Bvh
import kaloffl.spath.math.Color
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.math.Ray
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.structure.FlatObject
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d

/**
 * A scene holds all the objects that might be displayed, as well as the camera
 * from that the image is rendered.
 */
class Scene(
    val root: SceneNode,
    val camera: Camera,
    val airMedium: Material,
    val skyMaterial: Material,
    val skyDistance: Double = Double.PositiveInfinity) {

  /**
   * Tries to find an Intersection of the given ray with the objects in the
   * scene. If none is found, null is returned.
   */
  def getIntersection(ray: Ray, maxDist: Double): Intersection = {
    root.getIntersection(ray, maxDist)
  }
}