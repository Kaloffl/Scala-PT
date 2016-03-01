package kaloffl.spath.scene.materials

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import java.util.function.DoubleSupplier
import kaloffl.spath.math.Vec2d
import kaloffl.spath.scene.SurfaceInfo

class SpecularMaterial(base: Material,
                       refractiveIndex: Float,
                       roughness: Double = 0.0)
    extends Material(Color.Black, Color.Black, DummyFunction) {

  override def getInfo(incomingNormal: Vec3d,
                       surfaceNormal: ⇒ Vec3d,
                       textureCoordinate: ⇒ Vec2d,
                       airRefractiveIndex: Float,
                       random: DoubleSupplier): SurfaceInfo = {

    val axis = surfaceNormal.randomConeSample(Vec2d.random(random), roughness, 0.0)
    val weight = incomingNormal.refractance(axis, airRefractiveIndex, refractiveIndex)
    if (weight > random.getAsDouble) {
      new SurfaceInfo(
        Color.White,
        ReflectFunction.outDirections(
          incomingNormal,
          axis,
          airRefractiveIndex,
          random))
    } else {
      base.getInfo(incomingNormal, surfaceNormal, textureCoordinate, airRefractiveIndex, random)
    }
  }
}
