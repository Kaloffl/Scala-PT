package kaloffl.spath.tracing

import kaloffl.spath.scene.SceneObject
import kaloffl.spath.scene.Material
import kaloffl.spath.scene.shapes.Shape

/**
 * The intersection stores the depth measured from the start of a ray and the
 * shape that was hit by the ray.
 */
class Intersection(val depth: Double, val hitShape: Shape, val material: Material)