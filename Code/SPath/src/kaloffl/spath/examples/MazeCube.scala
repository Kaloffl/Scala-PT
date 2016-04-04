package kaloffl.spath.examples

import java.util.concurrent.ThreadLocalRandom
import java.util.function.DoubleSupplier
import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.PinholeCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.Viewpoint
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.DirectionalSky
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.PathTracer
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.materials.UniformSky
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.filter.BloomFilter
import kaloffl.spath.tracing.RecursivePathTracer
import kaloffl.spath.scene.hints.GlobalHint
import kaloffl.spath.scene.hints.LocalHint
import kaloffl.spath.scene.hints.ExclusionHint

object MazeCube {

  def main(args: Array[String]): Unit = {
    val rng = new DoubleSupplier() {
      override def getAsDouble(): Double = ThreadLocalRandom.current.nextDouble
    }

    val width = 21
    val height = 21
    val depth = 21

    val array = new Array[Boolean](width * height * depth)
    def isInRange(x: Int, y: Int, z: Int): Boolean = {
      return x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth
    }
    def getValueAt(x: Int, y: Int, z: Int): Boolean = {
      if (isInRange(x, y, z)) {
        array(x * height * depth + y * depth + z)
      } else {
        false
      }
    }
    def setValueAt(x: Int, y: Int, z: Int, value: Boolean): Unit = {
      if (isInRange(x, y, z)) {
        array(x * height * depth + y * depth + z) = value
      }
    }
    val directions = Array((1, 0, 0), (-1, 0, 0), (0, 1, 0), (0, -1, 0), (0, 0, 1), (0, 0, -1))

    def makeMaze(x: Int, y: Int, z: Int): Unit = {
      setValueAt(x, y, z, true)
      val closedDirections = new Array[Boolean](6)
      var numDirections = 6

      while (numDirections > 0) {
        val dirIndex = (rng.getAsDouble * 6).toInt
        if (!closedDirections(dirIndex)) {
          numDirections -= 1
          closedDirections(dirIndex) = true
          val dir = directions(dirIndex)
          val newX = x + dir._1 * 2
          val newY = y + dir._2 * 2
          val newZ = z + dir._3 * 2
          if (isInRange(newX, newY, newZ) && !getValueAt(newX, newY, newZ)) {
            setValueAt(x + dir._1, y + dir._2, z + dir._3, true)
            makeMaze(x + dir._1 * 2, y + dir._2 * 2, z + dir._3 * 2)
          }
        }
      }
    }
    makeMaze(0, 0, 0)

    val lightSource = new Sphere(Vec3d.Origin, 6)
    val fogArea = new Sphere(Vec3d.Origin, 21)
    val airMaterial = new TransparentMaterial(
      color = Color(0.9f, 0.9f, 0.9f),
      absorbtionDepth = 100,
      scatterProbability = 0.01f)

    val hemisphere =
      SceneNode(Seq(
        (for (x <- 0 until width; y <- 0 until height; z <- 0 until depth) yield {
          if (getValueAt(x, y, z) && (x <= 4 || x >= 16 || y <= 4 || y >= 16 || z <= 4 || z >= 16)) {
            val pos = Vec3d(x - width / 2, y - height / 2, z - depth / 2)
            SceneNode(
              AABB(pos, Vec3d.Unit),
              if (x == 20 || y == 20 || z == 20) {
                new TransparentMaterial(
                		color = Color(0.01f, 0.01f, 0.01f),
                		absorbtionDepth = 1f,
                		scatterProbability = 40,
                		refractiveIndex = 2,
                		glossiness = 0.002f)
              } else {
              	  DiffuseMaterial(Color(0.99f, 0.99f, 0.99f))
              }
          )
          } else {
            null
          }
        }),
        Seq(
          SceneNode(lightSource, LightMaterial(Color.White * 40f)),
          SceneNode(fogArea, airMaterial))).flatten.filter(_ != null).toArray)

    RenderEngine.render(
      bounces = 8,
      samplesAtOnce = 8,
      cpuSaturation = 0.5f,
      target = new JfxDisplay(1280, 720),
      tracer = RecursivePathTracer,
      view = new Viewpoint(
        position = Vec3d(20, 20, 20),
        forward = Vec3d(-1, -1, -1).normalize,
        up = Vec3d(-1, 1, -1).normalize),
      scene = new Scene(
        root = hemisphere,
        initialMediaStack = Array(airMaterial),
        lightHints = Array(new ExclusionHint(AABB(Vec3d.Origin, Vec3d(20, 20, 20)), lightSource)),
        camera = new PinholeCamera))
  }
}