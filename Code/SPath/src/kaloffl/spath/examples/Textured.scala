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
import kaloffl.spath.scene.materials.DiffuseTexturedMaterial
import kaloffl.spath.scene.materials.LazyTexture
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode

object Textured {

  def main(args: Array[String]): Unit = {

    val matAir = new TransparentMaterial(Color.Black, 0.0, 0.0)
    val matSky = new LightMaterial(Color.White, Attenuation.none)

    val image = ImageIO.read(new File("D:/temp/texture.jpg"))
    
    val matTexture = new DiffuseTexturedMaterial(new LazyTexture(image))
    
    val outdoor = SceneNode(new Sphere(Vec3d(0, 0, 0), 2), matTexture)
//    val outdoor = SceneNode(AABB(Vec3d(0, 0, 0), Vec3d(2)), matTexture)

    RenderEngine.render(
      bounces = 12,
      target = new Display(1280, 720),
      scene = new Scene(
        root = outdoor,
        airMedium = matAir,
        skyMaterial = matSky,
        camera = new Camera(
          position = Vec3d(0, 0, -4),
          forward = Vec3d(0, 0, 1).normalize,
          up = Vec3d(0, 1, 0).normalize)))
  }
}