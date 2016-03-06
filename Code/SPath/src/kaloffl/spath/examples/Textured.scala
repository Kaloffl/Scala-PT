package kaloffl.spath.examples

import java.io.File

import javax.imageio.ImageIO
import kaloffl.spath.JfxDisplay
import kaloffl.spath.RenderEngine
import kaloffl.spath.filter.BloomFilter
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.PinholeCamera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.Viewpoint
import kaloffl.spath.scene.hints.GlobalHint
import kaloffl.spath.scene.hints.LocalHint
import kaloffl.spath.scene.materials.BlackSky
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.DiffuseTexturedMaterial
import kaloffl.spath.scene.materials.LazyTexture
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.MaskedMaterial
import kaloffl.spath.scene.materials.SpecularMaterial
import kaloffl.spath.scene.materials.TextureMask
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.BoundedNormalMappedShape
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.RecursivePathTracer

object Textured {

  def main(args: Array[String]): Unit = {

    val atmosphereColor = Color(1f - 79f / 255f, 1f - 96f / 255f, 1f - 202 / 255f)

    val matVoid = new TransparentMaterial(
      color = Color.Black,
      scatterProbability = 0)
    val matAir1 = new TransparentMaterial(
      color = atmosphereColor * 4e-6f,
      scatterProbability = 4e-8)
    val matAir2 = new TransparentMaterial(
      color = atmosphereColor * 2e-6f,
      scatterProbability = 2e-8)
    val matAir3 = new TransparentMaterial(
      color = atmosphereColor * 1e-6f,
      scatterProbability = 1e-8)
    val matAir4 = new TransparentMaterial(
      color = atmosphereColor * 5e-7f,
      scatterProbability = 5e-9)
    val matAir5 = new TransparentMaterial(
      color = atmosphereColor * 25e-8f,
      scatterProbability = 25e-10)
    val matLight = LightMaterial(Color.White * 640000)

    val matWhite = DiffuseMaterial(Color(0.93f, 0.93f, 0.93f))
    val matGreen = DiffuseMaterial(Color(0.1f, 0.7f, 0.2f))
    val matBlack = DiffuseMaterial(Color.Black)

    val surface = ImageIO.read(new File("C:/dev/world.200409.3x5400x2700.jpg"))
    val normals = ImageIO.read(new File("C:/dev/EarthNormal.png"))
    val mask = ImageIO.read(new File("C:/dev/mask.jpg"))
    val clouds = ImageIO.read(new File("C:/dev/clouds.jpg"))

    val matTexture = new DiffuseTexturedMaterial(new LazyTexture(surface))
    val matWater = new SpecularMaterial(
      base = matTexture,
      refractiveIndex = 1.3f,
      roughness = 0.1)
    val matSurface = new MaskedMaterial(matWater, matTexture, new TextureMask(new LazyTexture(mask)))
    val matCloud = new MaskedMaterial(matWhite, matAir1, new TextureMask(new LazyTexture(clouds)))

    val earthRadius = 12756320 / 2
    val sunRadius = 1392684000 / 2
    val moonRadius = 3476000 / 2
    val earthSunDistance = 149.6e9
    val earthMoonDistance = 3844e5

    val sunSphere = new Sphere(Vec3d(1, 0, 1).normalize * earthSunDistance, sunRadius)
    val moonSphere = new Sphere(Vec3d(1, 0, -1).normalize * earthMoonDistance, moonRadius)

    val earthArea = new Sphere(Vec3d.Origin, earthRadius + 2e5f)
    val moonArea = new Sphere(moonSphere.position, moonRadius + 10)

    val world = SceneNode(Array(
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 2e5f), matAir4),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 15e4f), matAir3),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 1e5f), matAir2),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 5e4f), matAir1),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 2e4f), matCloud),
      SceneNode(
        new BoundedNormalMappedShape(
          new Sphere(Vec3d.Origin, earthRadius),
          new LazyTexture(normals)),
        matSurface),
      SceneNode(moonSphere, matWhite),
      SceneNode(sunSphere, matLight)))

    val display = new JfxDisplay(1280, 720)
    //    val position = Vec3d(0.5, 0, 1).normalize * (earthRadius * 2)
    val position = Vec3d(0, 1, 0).normalize * (earthRadius * 1.8) + Vec3d(1, 0, 1) * (earthRadius * 0.2)
    RenderEngine.render(
      bounces = 20,
      target = new BloomFilter(display, 10, 0.5f),
      tracer = RecursivePathTracer,
      view = new Viewpoint(
        position = position,
        forward = Vec3d.Down,
        up = Vec3d(1, 0, 1).normalize),
      scene = new Scene(
        root = world,
        initialMediaStack = Array(matVoid),
        lightHints = Array(
          GlobalHint(sunSphere),
          LocalHint(earthArea, moonSphere)),
        skyMaterial = BlackSky,
        camera = new PinholeCamera))
  }
}