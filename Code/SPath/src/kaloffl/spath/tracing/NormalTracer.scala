package kaloffl.spath.tracing

import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.math.Color
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.math.Ray
import java.util.function.DoubleSupplier

class NormalTracer(val scene: Scene) extends Tracer {

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
      val surfaceNormal = intersection.normal()
      if(surfaceNormal.lengthSq > 1.1) {
        println(surfaceNormal)
        return Color.Red
      }
      return Color(surfaceNormal / 2 + 0.5)
    }
  }
}