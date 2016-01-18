package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Vec2d
import kaloffl.spath.scene.SurfaceInfo

class TransparentMaterial(
    color: Color,
    override val scatterProbability: Double = 0,
    override val refractiveIndex: Float = 1,
    roughness: Double = 0) extends Material(Color.White, new RefractFunction(refractiveIndex, roughness)) {

  override def getAbsorbtion(worldPos: Vec3d, context: Context): Color = color
}