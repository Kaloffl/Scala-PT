package kaloffl.spath.scene

import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.math.Vec3d

/**
 * A SceneObject is a combination of a material and a shape. As of now this
 * separation of SceneObject and Shape is of little use, other that the
 * separation of the concerns of a displayable object and a geometric construct
 */
class SceneObject(val shapes: Array[Shape], val material: Material) {
  
  def this(shape: Shape, material: Material) {
    this(Array(shape), material)
  }
}