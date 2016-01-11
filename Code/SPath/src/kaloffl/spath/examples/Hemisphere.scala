package kaloffl.spath.examples

import java.util.concurrent.ThreadLocalRandom
import java.util.function.DoubleSupplier

import kaloffl.spath.Display
import kaloffl.spath.RenderEngine
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.DirectionalLightMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode

object Hemisphere {

  def main(args: Array[String]): Unit = {
    val rng = new DoubleSupplier() {
      override def getAsDouble(): Double = ThreadLocalRandom.current.nextDouble
    }
    val display = new Display(1280, 720)

    val matRedDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matYellowDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.1f))
    val matCyanDiffuse = DiffuseMaterial(Color(0.1f, 0.9f, 0.9f))
    val matPinkDiffuse = DiffuseMaterial(Color(0.9f, 0.1f, 0.9f))
    val matBlackDiffuse = DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matGrayDiffuse = DiffuseMaterial(Color(0.5f, 0.5f, 0.5f))
    val matAir = new TransparentMaterial(Color.WHITE, 0.0, 0.0, 1.0)

    val diffuseMaterials = Array(
      matRedDiffuse,
      matGreenDiffuse,
      matBlueDiffuse,
      matYellowDiffuse,
      matCyanDiffuse,
      matPinkDiffuse,
      matWhiteDiffuse,
      matGrayDiffuse,
      matBlackDiffuse)

    val hemisphere = SceneNode((for (i ← 0 to 2000) yield {
      val weight = Math.cos(rng.getAsDouble * Math.PI / 2)
      val rhs = Vec3d.BACK.weightedHemisphere(Vec2d.random(rng))
      SceneNode(
        new Sphere(rhs, 0.025f),
        diffuseMaterials((rng.getAsDouble * diffuseMaterials.length).toInt))
    }).toArray)

    val closerLowCam = new Camera(Vec3d(0, 0, 2.2), Vec3d(0, 0, -1).normalize, Vec3d.LEFT, 0, 3)

    val hemisphereScene = new Scene(
        root = hemisphere, 
        camera = closerLowCam, 
        airMedium = matAir, 
        skyMaterial = new DirectionalLightMaterial(Color.WHITE * 2, Vec3d(0, 1, -3).normalize, 0.5f))
    RenderEngine.render(target = display, scene = hemisphereScene, bounces = 12)
  }
}