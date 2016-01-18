package kaloffl.spath.scene

import kaloffl.spath.math.Ray
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.Intersection

/**
 * A scene holds all the objects that might be displayed, as well as the camera
 * from that the image is rendered.
 */
class Scene(val root: SceneNode,
            val camera: Camera,
            val initialMediaStack: Array[Material],
            val skyMaterial: Material,
            val skyDistance: Double = Double.PositiveInfinity) {

  def this(root: SceneNode,
           camera: Camera,
           airMedium: Material,
           skyMaterial: Material,
           skyDistance: Double) {
    this(root, camera, Array(airMedium), skyMaterial, skyDistance)
  }

  def this(root: SceneNode,
           camera: Camera,
           airMedium: Material,
           skyMaterial: Material) {
    this(root, camera, Array(airMedium), skyMaterial)
  }

  /**
   * Tries to find an Intersection of the given ray with the objects in the
   * scene. If none is found, null is returned.
   */
  def getIntersection(ray: Ray, maxDist: Double): Intersection = {
    root.getIntersection(ray, maxDist)
  }
}