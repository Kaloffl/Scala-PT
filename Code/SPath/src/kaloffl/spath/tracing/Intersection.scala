package kaloffl.spath.tracing

import kaloffl.spath.scene.SceneObject

/**
 * The intersection stores the depth measured from the start of a ray and the
 * object that was hit by the ray.
 *
 * @author Lars Donner
 */
class Intersection(val depth: Double, val hitObject: SceneObject) {

}