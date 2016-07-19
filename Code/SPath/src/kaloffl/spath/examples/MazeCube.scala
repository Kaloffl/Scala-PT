package kaloffl.spath.examples

import java.util.concurrent.ThreadLocalRandom
import java.util.function.DoubleSupplier

import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.scene.hints.GlobalHint
import kaloffl.spath.scene.materials.{DiffuseMaterial, EmittingMaterial, TransparentMaterial, UniformSky}
import kaloffl.spath.scene.shapes.{AABB, Sphere}
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.{PinholeCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.RecursivePathTracer
import kaloffl.spath.{JfxDisplay, RenderEngine}

object MazeCube {

  def main(args: Array[String]): Unit = {
    val rng = new DoubleSupplier() {
      override def getAsDouble: Double = ThreadLocalRandom.current.nextDouble
    }

    val width = 11
    val height = 11
    val depth = 11

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

    val lightSource = new Sphere(Vec3d.Origin, 2)
    val fogArea = new Sphere(Vec3d.Origin, 40)
    val airMaterial = new TransparentMaterial(
      volumeColor = Color(0.99f, 0.99f, 0.99f),
      absorbtionDepth = 100,
      scatterProbability = 0.0002f)

    val hemisphere =
      SceneNode(Seq(
        for (x <- 0 until width; y <- 0 until height; z <- 0 until depth) yield {
          if (getValueAt(x, y, z) && (x <= 2 || x >= 8 || y <= 2 || y >= 8 || z <= 2 || z >= 8)) {
            val pos = Vec3d(x * 2 - width, y * 2 - height, z * 2 - depth)
            SceneNode(
              AABB(pos, Vec3d(2, 2, 2)), DiffuseMaterial(Color(0.5f, 0.5f, 0.5f))
          )
          } else {
            null
          }
        },
        Seq(
          SceneNode(lightSource, new EmittingMaterial(Color.White, 2000f))
          )).flatten.filter(_ != null).toArray)

    val window = new JfxDisplay(1280, 720)

    RenderEngine.render(
      bounces = 16,
      target = window,
      tracer = RecursivePathTracer,
      view = new Viewpoint(
        position = Vec3d(20, 20, 20),
        forward = Vec3d(-1, -1, -1).normalize,
        up = Vec3d(-1, 1, -1).normalize),
      scene = new Scene(
        root = hemisphere,
//        initialMediaStack = Array(airMaterial),
        lightHints = Array(GlobalHint(lightSource)),
        skyMaterial = new UniformSky(Color.White),
        camera = new PinholeCamera))
  }
}