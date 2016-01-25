package kaloffl.spath.tracing

import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.math.Color
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.math.Ray
import java.util.function.DoubleSupplier

class TexcoordTracer(val scene: Scene) extends Tracer {

  override def trace(x: Float,
                     y: Float,
                     maxBounces: Int,
                     random: DoubleSupplier): Color = {
    var ray = scene.camera.createRay(random, x, y)

    val intersection = scene.getIntersection(ray, Double.PositiveInfinity)
    if (!intersection.hitObject) {
      return Color.Black
    } else {
      val depth = intersection.depth
      val texcoord = intersection.textureCoordinate()
      return Color(texcoord.x.toFloat, texcoord.y.toFloat, 0)
    }
  }
}