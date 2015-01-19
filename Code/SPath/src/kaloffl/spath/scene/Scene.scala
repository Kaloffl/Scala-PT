package kaloffl.spath.scene

import kaloffl.spath.tracing.Ray
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.math.Vec3f

class Scene(objectsSeq: Seq[SceneObject], val camera: Camera) {
  val objects: Array[SceneObject] = objectsSeq.toArray
  val shapes: Array[Shape] = objects.map { o ⇒ o.shape }

  def getIntersection(ray: Ray): Intersection = {
    var minDepth = Float.MaxValue
    var hitIndex: Int = -1

    {
      var i = 0
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