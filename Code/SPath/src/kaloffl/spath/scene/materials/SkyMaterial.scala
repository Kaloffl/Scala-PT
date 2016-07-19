package kaloffl.spath.scene.materials

import kaloffl.spath.math.{Color, Vec3d}

trait SkyMaterial {
  def getEmittance(incomingNormal: Vec3d): Color
}

object BlackSky extends SkyMaterial {
  override def getEmittance(incomingNormal: Vec3d): Color = Color.Black
}

class UniformSky(color: Color) extends SkyMaterial {
  override def getEmittance(incomingNormal: Vec3d): Color = color
}

class DirectionalSky(color: Color, direction: Vec3d, limit: Float) extends SkyMaterial {
  override def getEmittance(incomingNormal: Vec3d): Color = {
    val angle = Math.max(limit, incomingNormal.dot(direction).toFloat)
    if (1 == limit) {
      if (1 == angle) {
        return color
      }
      return Color.Black
    }
    return color * (1 - (1 - angle) / (1 - limit))
  }
}

class TexturedSky(val texture: (Float, Float) => Color) extends SkyMaterial {
  override def getEmittance(incomingNormal: Vec3d): Color = {
    val u = (Math.atan2(incomingNormal.x, incomingNormal.z) / (2 * Math.PI) + 0.5).toFloat
    val v = (Math.asin(-incomingNormal.y) / Math.PI + 0.5).toFloat
    return texture(u, v)
  }
}