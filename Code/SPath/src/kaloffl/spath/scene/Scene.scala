package kaloffl.spath.scene

import kaloffl.spath.tracing.Ray
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.math.Vec3d
import kaloffl.spath.bvh.Bvh

/**
 * A scene holds all the objects that might be displayed, as well as the camera
 * from that the image is rendered.
 */
class Scene(objects: Array[SceneObject], val camera: Camera) {
  val lights: Array[SceneObject] = objects.filter { _.material.terminatesPath }
  val bvh = createBvh

  def createBvh: Bvh = {
    printf("Building BVH for %d primitives.\n", objects.length)

    val start = System.nanoTime
    val bvh = new Bvh(objects)
    val duration = System.nanoTime - start

    println("Done.")
    if (duration > 1000000000) {
      println("buildtime: " + Math.floor(duration / 10000000.0) / 100.0 + "s")
    } else {
      println("buildtime: " + Math.floor(duration / 10000.0) / 100.0 + "ms")
    }

    return bvh
  }

  /**
   * Tries to find an Intersection of the given ray with the objects in the
   * scene. If none is found, null is returned.
   */
  def getIntersection(ray: Ray): Intersection = {
    bvh.getIntersection(ray)
  }
}