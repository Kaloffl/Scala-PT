package kaloffl.spath.scene.materials

import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import java.util.function.DoubleSupplier

trait ScatterFunction {
  def outDirections(inDirection: Vec3d,
                    normal: Vec3d,
                    airIndex: Float,
                    random: DoubleSupplier): Scattering
}

object DummyFunction extends ScatterFunction {
  override def outDirections(inDirection: Vec3d,
                             normal: Vec3d,
                             airIndex: Float,
                             random: DoubleSupplier): Scattering = ???
}

object DiffuseFunction extends ScatterFunction {
  override def outDirections(inDirection: Vec3d,
                             normal: Vec3d,
                             airIndex: Float,
                             random: DoubleSupplier): Scattering = {
    if (inDirection.dot(normal) < 0) {
      new SingleRayScattering(normal.weightedHemisphere(Vec2d.random(random)))
    } else {
      new SingleRayScattering(-normal.weightedHemisphere(Vec2d.random(random)))
    }
  }
}

object ReflectFunction extends ScatterFunction {
  override def outDirections(inDirection: Vec3d,
                             normal: Vec3d,
                             airIndex: Float,
                             random: DoubleSupplier): Scattering = {
    new SingleRayScattering(inDirection.reflect(normal))
  }
}

class GlossyReflectFunction(glossiness: Double) extends ScatterFunction {
  override def outDirections(inDirection: Vec3d,
                             normal: Vec3d,
                             airIndex: Float,
                             random: DoubleSupplier): Scattering = {
    new SingleRayScattering(inDirection.reflect(normal.randomConeSample(Vec2d.random(random), glossiness, 0.0)))
  }
}

class RefractFunction(refractiveIndex: Float,
                      glossiness: Double = 0.0) extends ScatterFunction {
  override def outDirections(inDirection: Vec3d,
                             normal: Vec3d,
                             airIndex: Float,
                             random: DoubleSupplier): Scattering = {

    val axis = normal.randomConeSample(Vec2d.random(random), glossiness, 0.0)
    if (axis.dot(inDirection) > 0) {
      new RefractiveScattering(inDirection, -axis, refractiveIndex, airIndex)
    } else {
      new RefractiveScattering(inDirection, axis, airIndex, refractiveIndex)
    }
  }
}