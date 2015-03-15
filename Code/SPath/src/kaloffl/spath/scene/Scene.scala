package kaloffl.spath.scene

import kaloffl.spath.tracing.Ray
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.math.Vec3d

/**
 * A scene holds all the objects that might be displayed, as well as the camera
 * from that the image is rendered.
 */
class Scene(objectsSeq: Seq[SceneObject], val camera: Camera) {
  val objects: Array[SceneObject] = objectsSeq.toArray
  val shapes: Array[Shape] = objects.map { _.shape }
  val lights: Array[SceneObject] = objects.filter { _.material.terminatesPath }
  val maxEmittance: Vec3d = lights.foldLeft(Vec3d.BLACK) { (c, o) ⇒
    Vec3d(
      Math.max(c.x, o.material.maxEmittance.x),
      Math.max(c.y, o.material.maxEmittance.y),
      Math.max(c.z, o.material.maxEmittance.z))
  }

  /**
   * Tries to find an Intersection of the given ray with the objects in the
   * scene. If none is found, null is returned.
   */
  def getIntersection(ray: Ray): Intersection = {
    var minDepth: Double = Double.PositiveInfinity
    var hitIndex: Int = -1

    {
      var i: Int = 0
      while (i < shapes.length) {
        val s = shapes(i)
        val depth = s.getIntersectionDepth(ray)
        if (depth < minDepth) {
          minDepth = depth
          hitIndex = i
        }
        i += 1
      }
    }
    if (0 > hitIndex) return null

    return new Intersection(minDepth, objects(hitIndex))
  }
}