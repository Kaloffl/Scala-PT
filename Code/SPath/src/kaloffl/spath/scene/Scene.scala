package kaloffl.spath.scene

import kaloffl.spath.math.{Color, Ray}
import kaloffl.spath.scene.hints.LightHint
import kaloffl.spath.scene.materials.{BlackSky, Material, SkyMaterial, TransparentMaterial}
import kaloffl.spath.scene.shapes.{Emitter, Shape}
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.Intersection

/**
 * A scene holds all the objects that might be displayed, as well as the camera
 * from that the image is rendered.
 */
class Scene(val root: SceneNode,
            val camera: Camera,
            val initialMediaStack: Array[Material] = Array(new TransparentMaterial()),
            val skyMaterial: SkyMaterial = BlackSky,
            val skyDistance: Double = Double.PositiveInfinity,
            val lightHints: Array[LightHint] = Array()) {

  val lightSources: Array[(Shape with Emitter, Material)] = {
    root.getShapes
      .filter(pair =>
        pair._2.emission != Color.Black &&
        pair._1.isInstanceOf[Emitter])
      .map(pair =>
        (pair._1.asInstanceOf[Shape with Emitter], pair._2))
      .toArray
  }

  /**
   * Tries to find an Intersection of the given ray with the objects in the
   * scene. If none is found, null is returned.
   */
  def getIntersection(ray: Ray, maxDist: Double): Intersection = {
    root.getIntersection(ray, maxDist)
  }
}