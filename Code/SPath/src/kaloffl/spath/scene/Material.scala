package kaloffl.spath.scene

import kaloffl.spath.math.Vec3f

class Material(val emittance: Vec3f,
               val reflectance: Vec3f,
               val reflectivity: Float,
               val refractivity: Float,
               val refractivityIndex: Float,
               val glossiness: Float) {

}