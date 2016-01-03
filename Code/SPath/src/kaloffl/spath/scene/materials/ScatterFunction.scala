package kaloffl.spath.scene.materials

import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import java.util.function.DoubleSupplier

trait ScatterFunction {
  def outDirection(inDirection: Vec3d,
                   normal: Vec3d,
                   airIndex: Double,
                   random: DoubleSupplier): Vec3d
}

object DummyFunction extends ScatterFunction {
  override def outDirection(inDirection: Vec3d,
                            normal: Vec3d,
                            airIndex: Double,
                            random: DoubleSupplier): Vec3d = ???
}

object DiffuseFunction extends ScatterFunction {
  override def outDirection(inDirection: Vec3d,
                            normal: Vec3d,
                            airIndex: Double,
                            random: DoubleSupplier): Vec3d = {
    normal.weightedHemisphere(Vec2d.random(random))
  }
}

object ReflectFunction extends ScatterFunction {
  override def outDirection(inDirection: Vec3d,
                            normal: Vec3d,
                            airIndex: Double,
                            random: DoubleSupplier): Vec3d = {
    inDirection.reflect(normal)
  }
}

class GlossyReflectFunction(glossiness: Double) extends ScatterFunction {
  override def outDirection(inDirection: Vec3d,
                            normal: Vec3d,
                            airIndex: Double,
                            random: DoubleSupplier): Vec3d = {
    inDirection.reflect(normal.randomConeSample(Vec2d.random(random), glossiness, 0.0))
  }
}

class RefractFunction(refractivityIndex: Double,
                      glossiness: Double = 0.0) extends ScatterFunction {
  override def outDirection(inDirection: Vec3d,
                            normal: Vec3d,
                            airIndex: Double,
                            random: DoubleSupplier): Vec3d = {

    val axis = normal.randomConeSample(Vec2d.random(random), glossiness, 0.0)
    if (axis.dot(inDirection) > 0) {
      val surf = -axis
      if (random.getAsDouble < inDirection.refractance(surf, refractivityIndex, airIndex)) {
        inDirection.reflect(surf)
      } else {
        inDirection.refract(surf, refractivityIndex, airIndex)
      }
    } else {
      if (random.getAsDouble < inDirection.refractance(axis, airIndex, refractivityIndex)) {
        inDirection.reflect(axis)
      } else {
        inDirection.refract(axis, airIndex, refractivityIndex)
      }
    }
  }
}