package kaloffl.spath.examples

import java.io.File
import javax.imageio.ImageIO
import kaloffl.spath.Display
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LazyTexture
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.materials.UniformSky
import kaloffl.spath.scene.shapes.NormalMappedShape
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.tracing.NormalTracer
import kaloffl.spath.tracing.RecursivePathTracer
import kaloffl.spath.scene.materials.DiffuseTexturedMaterial
import kaloffl.spath.scene.materials.MaskedMaterial
import kaloffl.spath.scene.materials.TextureMask
import kaloffl.spath.tracing.PathTracer
import kaloffl.spath.scene.materials.SpecularMaterial
import kaloffl.spath.tracing.TexcoordTracer

object Textured {

  def main(args: Array[String]): Unit = {

    val atmosphereColor = Color(1f - 79f / 255f, 1f - 96f / 255f, 1f - 202 / 255f)

    val matVoid = new TransparentMaterial(
      color = Color.Black,
      scatterProbability = 0,
      refractiveIndex = 1,
      roughness = 0)
    val matAir1 = new TransparentMaterial(
      color = atmosphereColor * 4e-6f,
      scatterProbability = 8e-8,
      refractiveIndex = 1,
      roughness = 0)
    val matAir2 = new TransparentMaterial(
      color = atmosphereColor * 2e-6f,
      scatterProbability = 4e-8,
      refractiveIndex = 1,
      roughness = 0)
    val matAir3 = new TransparentMaterial(
      color = atmosphereColor * 1e-6f,
      scatterProbability = 2e-8,
      refractiveIndex = 1,
      roughness = 0)
    val matAir4 = new TransparentMaterial(
      color = atmosphereColor * 5e-7f,
      scatterProbability = 1e-8,
      refractiveIndex = 1,
      roughness = 0)
    val matAir5 = new TransparentMaterial(
      color = atmosphereColor * 25e-8f,
      scatterProbability = 5e-9,
      refractiveIndex = 1,
      roughness = 0)
    val matLight = new LightMaterial(Color.White * 40, Attenuation.none)

    val matWhite = DiffuseMaterial(Color(0.8f, 0.8f, 0.8f))
    val matBlack = DiffuseMaterial(Color.Black)

    val surface = ImageIO.read(new File("D:/temp/world.topo.200411.3x5400x2700.jpg"))
    val normals = ImageIO.read(new File("D:/temp/EarthNormal.png"))
    val mask = ImageIO.read(new File("D:/temp/mask.jpg"))
    val clouds = ImageIO.read(new File("D:/temp/clouds.jpg"))

    val matTexture = new DiffuseTexturedMaterial(new LazyTexture(surface))
    val matWater = new SpecularMaterial(
      base = matTexture,
      refractiveIndex = 1.3f,
      roughness = 0.1)
    val matSurface = new MaskedMaterial(matWater, matTexture, new TextureMask(new LazyTexture(mask)))

    val matCloud = new MaskedMaterial(matWhite, matAir1, new TextureMask(new LazyTexture(clouds)))
    val earthRadius = 12756320 / 2
    val sunRadius = 1392684000 / 2
    val moonRadius = 3476 / 2
    //    val earthSunDistance = 149.6e9
    val earthSunDistance = 149.6e7
    val earthMoonDistance = 3844e5

    val world = SceneNode(Array(
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 2e5f), matAir4),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 15e4f), matAir3),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 1e5f), matAir2),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 5e4f), matAir1),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 2e4f), matCloud),
      SceneNode(
        new NormalMappedShape(
          new Sphere(Vec3d.Origin, earthRadius),
          new LazyTexture(normals)),
        matSurface),
      SceneNode(new Sphere(Vec3d(1, 0, 1).normalize * earthSunDistance, sunRadius), matLight),
      SceneNode(new Sphere(Vec3d(1, 0, 1).normalize * earthMoonDistance, moonRadius), matWhite)))

    val display = new Display(1280, 720)
    val position = Vec3d(0.5, 0, 1).normalize * (earthRadius * 2)
    RenderEngine.render(
      bounces = 20,
      target = display,
      tracer = new RecursivePathTracer(new Scene(
        root = world,
        airMedium = matVoid,
        skyMaterial = new UniformSky(Color.White / 256),
        camera = new Camera(
          position = position,
          forward = -position.normalize,
          up = Vec3d.Up))))
  }
}