package kaloffl.spath.examples

import java.io.File
import javax.imageio.ImageIO

import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.scene.materials._
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.{PinholeCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.RecursivePathTracer
import kaloffl.spath.{JfxDisplay, RenderEngine}

object MaterialTest {

  def main(args: Array[String]): Unit = {

    val skyTexture = new LazyTexture(ImageIO.read(new File("C:/dev/clouds.jpg")))
    val matSky = new TexturedSky(texture = skyTexture.apply(_, _) * 4 + 0.5f)

    val dielectricNodes = SceneNode(
      (for (x <- 0 until 5; y <- 0 until 5) yield {
        SceneNode(
          new Sphere(Vec3d(0, -5 + 2.5 * y, -5 + 2.5 * x), 1),
          new DielectricMaterial(
            albedo = (u, v) => Color(0.9f, 0.1f, 0.1f),
            roughness = (u, v) => 0.25f * x,
            ior = 1 + 0.2f * y
          ))
      }).toArray
    )

    val transparentNodes = SceneNode(
      (for (x <- 0 until 5; y <- 0 until 5) yield {
        SceneNode(
          new Sphere(Vec3d(0, -5 + 2.5 * y, -5 + 2.5 * x), 1),
          new TransparentMaterial(
            volumeColor = Color(0.9f, 0.1f, 0.1f),
            roughness = (u, v) => 0.25f * x,
            //scatterProbability = 1f * y,
            ior = 1 + 0.2f * y
          ))
      }).toArray
    )

    val metalColors = Array(
      new Color(1, 0.782f, 0.344f),
      new Color(0.972f, 0.96f, 0.915f),
      new Color(0.955f, 0.638f, 0.538f),
      new Color(0.562f, 0.565f, 0.578f),
      new Color(0.542f, 0.497f, 0.449f)
    )

    val metalNodes = SceneNode(
      (for (x <- 0 until 5; y <- 0 until 5) yield {
        SceneNode(
          new Sphere(Vec3d(0, -5 + 2.5 * y, -5 + 2.5 * x), 1),
          new MetalMaterial(
            color = (u, v) => metalColors(y),
            roughness = (u, v) => 0.25f * x
          ))
      }).toArray
    )

    RenderEngine.render(
      target = new JfxDisplay(1024, 1024),
      tracer = new RecursivePathTracer(maxBounces = 10),
      view = new Viewpoint(
        position = Vec3d(-14, 0, 0),
        forward = Vec3d(1, 0, 0).normalize,
        up = Vec3d.Up),
      scene = new Scene(
        root = dielectricNodes,
        //root = transparentNodes,
        //root = metalNodes,
        skyMaterial = matSky,
        camera = new PinholeCamera(
          sensorDistance = 0.01f
        )))
  }
}