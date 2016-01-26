package kaloffl.spath.scene.materials

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d

class TransparentMaterial(
    color: Color,
    override val scatterProbability: Double = 0,
    override val refractiveIndex: Float = 1,
    glossiness: Float = 0) extends Material(Color.White, new RefractFunction(refractiveIndex, glossiness)) {

  override def getAbsorbtion(worldPos: Vec3d, random: DoubleSupplier): Color = color
}