package kaloffl.spath.scene

import kaloffl.spath.scene.shapes.Shape

/**
 * A SceneObject is a combination of a material and a shape. As of now this
 * separation of SceneObject and Shape is of little use, other that the
 * separation of the concerns of a displayable object and a geometric construct
 *
 * @author Lars Donner
 */
class SceneObject(val shape: Shape, val material: Material) {

}