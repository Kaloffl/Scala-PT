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
import kaloffl.spath.scene.materials.DiffuseTexturedMaterial
import kaloffl.spath.scene.materials.LazyTexture
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.MaskedMaterial
import kaloffl.spath.scene.materials.TextureMask
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode

object Textured {

  def main(args: Array[String]): Unit = {

    val atmosphereColor = Color(1f - 79f / 255f, 1f - 96f / 255f, 1f - 202 / 255f)

    val matVoid = new TransparentMaterial(
      color = Color.Black,
      scatterProbability = 0,
      refractiveIndex = 0.99f,
      roughness = 0)
    val matAir1 = new TransparentMaterial(
      color = atmosphereColor * 4e-6f,
      scatterProbability = 8e-8,
      refractiveIndex = 1.0f,
      roughness = 0)
    val matAir2 = new TransparentMaterial(
      color = atmosphereColor * 2e-6f,
      scatterProbability = 4e-8,
      refractiveIndex = 0.998f,
      roughness = 0)
    val matAir3 = new TransparentMaterial(
      color = atmosphereColor * 1e-6f,
      scatterProbability = 2e-8,
      refractiveIndex = 0.996f,
      roughness = 0)
    val matAir4 = new TransparentMaterial(
      color = atmosphereColor * 5e-7f,
      scatterProbability = 1e-8,
      refractiveIndex = 0.994f,
      roughness = 0)
    val matAir5 = new TransparentMaterial(
      color = atmosphereColor * 25e-8f,
      scatterProbability = 5e-9,
      refractiveIndex = 0.992f,
      roughness = 0)
    val matLight = new LightMaterial(Color.White * 40, Attenuation.none)

    val matWhite = DiffuseMaterial(Color.White)
    val matGray = DiffuseMaterial(Color(0.5f, 0.5f, 0.5f))
    
    val matSpace = new LightMaterial(Color.White / 64, Attenuation.none)

    val matWater = new TransparentMaterial(
      color = Color(4, 2, 0.5f) * 0.34f,
      scatterProbability = 1,
      refractiveIndex = 1.3f,
      roughness = 0.1)
    val image = ImageIO.read(new File("D:/temp/texture.jpg"))
    val mask = ImageIO.read(new File("D:/temp/mask.jpg"))
    val clouds = ImageIO.read(new File("D:/temp/clouds.jpg"))

    val matTexture = new DiffuseTexturedMaterial(new LazyTexture(image))
    val matCombined = new MaskedMaterial(matTexture, matWater, new TextureMask(new LazyTexture(mask)))

    val matCloud = new MaskedMaterial(matAir1, matWhite, new TextureMask(new LazyTexture(clouds)))
    val earthRadius = 6378160
    val sunRadius = 696342000
    //    val earthSunDistance = 149.6e9
    val earthSunDistance = 149.6e7

    val world = SceneNode(Array(
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 2e5f), matAir4),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 15e4f), matAir3),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 1e5f), matAir2),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 5e4f), matAir1),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius + 1e4f), matCloud),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius), matCombined),
      SceneNode(new Sphere(Vec3d.Origin, earthRadius - 3.7e3f), matGray),
      SceneNode(new Sphere(Vec3d(-earthSunDistance, 0, 0), sunRadius), matLight)))

    val position = Vec3d.Back * (earthRadius * 2)

    RenderEngine.render(
      bounces = 20,
      target = new Display(1280, 720),
      scene = new Scene(
        root = world,
        airMedium = matVoid,
        skyMaterial = matSpace,
        camera = new Camera(
          position = position,
          forward = Vec3d.Front,
          up = Vec3d.Up)))
  }
}