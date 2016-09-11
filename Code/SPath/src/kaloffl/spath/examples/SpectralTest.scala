package kaloffl.spath.examples

import java.io.File
import javax.imageio.ImageIO

import kaloffl.spath.math.Units._
import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.scene.materials._
import kaloffl.spath.scene.shapes.{AABB, Sphere}
import kaloffl.spath.scene.structure.{BoundlessNode, SceneNode}
import kaloffl.spath.scene.{LensCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.{BidirectionalPathTracer, PathTracer, RecursivePathTracer}
import kaloffl.spath.{JfxDisplay, RenderEngine}

object SpectralTest {

  def main(args: Array[String]): Unit = {
    val skyTexture = new LazyTexture(ImageIO.read(new File("C:/dev/clouds.jpg")))
    val matSky = new TexturedSky(texture = skyTexture.apply(_, _) * 4 + 0.5f)

    val colorWhite = Color(0.9f, 0.9f, 0.9f)
    val matWhiteDiffuse = DiffuseMaterial(colorWhite)

    val glassIor = new Color(1.44f, 1.45f, 1.46f)
    //val glassIor = new Color(1.45f, 1.45f, 1.45f)
    val matGlass = new TransparentMaterial(ior = glassIor)

    val objects = BoundlessNode(Array(
      SceneNode(new Sphere(Vec3d(0.0f, 2.0f, 0.0f), 2.0f), matGlass),
      SceneNode(AABB(Vec3d(0.0f, -0.5f, 0.0f), Vec3d(5, 1, 5)), matWhiteDiffuse)))

    val window = new JfxDisplay(512, 512)

    RenderEngine.render(
      target = window,
      tracer = new RecursivePathTracer(maxBounces = 8),
      //tracer = new PathTracer(maxBounces = 8),
      cpuSaturation = 0.25f,
      view = new Viewpoint(
        position = Vec3d(0, 2, 7),
        forward = Vec3d.Back,
        up = Vec3d.Up),
      scene = new Scene(
        root = objects,
        skyMaterial = matSky,
        camera = new LensCamera(
            sensorDistance = mm(1.5f),
            lensRadius = mm(7),
            focussedDepth = 8)))
  }
}