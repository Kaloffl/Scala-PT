package kaloffl.spath.scene

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.tracing.Context
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Vec2d

class TransparentMaterial(
    val color: Color,
    override val absorbtionCoefficient: Double = 1,
    override val scatterPropability: Double = 0,
    override val refractivityIndex: Double = 1,
    val roughness: Double = 0) extends Material {

  override def getAbsorbtion(worldPos: Vec3d, context: Context): Color = color

  override def getInfo(
    worldPos: Vec3d,
    surfaceNormal: Vec3d,
    incomingNormal: Vec3d,
    depth: Double,
    airRefractivityIndex: Double,
    context: Context): SurfaceInfo = {

    val (i1, i2, surf) = if (surfaceNormal.dot(incomingNormal) > 0) {
      (refractivityIndex, 1.0, -surfaceNormal)
    } else {
      (1.0, refractivityIndex, surfaceNormal)
    }

    val axis = surf.randomConeSample(Vec2d.random(context.random), roughness, 0)
    new SurfaceInfo(
      Color.WHITE,
      Color.BLACK,
      if (context.random.getAsDouble < incomingNormal.refractance(axis, i1, i2)) {
        incomingNormal.reflect(axis)
      } else {
        incomingNormal.refract(axis, i1, i2)
      })
  }
}