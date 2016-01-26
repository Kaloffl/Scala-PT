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
      new DiffuseScattering(normal, Vec2d.random(random))
    } else {
      new DiffuseScattering(-normal, Vec2d.random(random))
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

class GlossyReflectFunction(glossiness: Float) extends ScatterFunction {
  override def outDirections(inDirection: Vec3d,
                             normal: Vec3d,
                             airIndex: Float,
                             random: DoubleSupplier): Scattering = {
    new ReflectiveScattering(
      inDirection,
      normal,
      glossiness,
      Vec2d.random(random))
  }
}

class RefractFunction(refractiveIndex: Float,
                      glossiness: Float = 0f) extends ScatterFunction {
  override def outDirections(inDirection: Vec3d,
                             normal: Vec3d,
                             airIndex: Float,
                             random: DoubleSupplier): Scattering = {

    if (normal.dot(inDirection) > 0) {
      new RefractiveScattering(
        inDirection,
        -normal,
        refractiveIndex,
        airIndex,
        glossiness,
        Vec2d.random(random))
    } else {
      new RefractiveScattering(
        inDirection,
        normal,
        airIndex,
        refractiveIndex,
        glossiness,
        Vec2d.random(random))
    }
  }
}