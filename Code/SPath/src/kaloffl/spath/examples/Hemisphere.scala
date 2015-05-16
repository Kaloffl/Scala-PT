package kaloffl.spath.examples

import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Color
import java.util.function.DoubleSupplier
import java.util.concurrent.ThreadLocalRandom
import kaloffl.spath.PathTracer
import kaloffl.spath.Display
import kaloffl.spath.scene.Camera
import kaloffl.spath.math.Vec2d
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.DirectionalLightMaterial

object Hemisphere {

  def main(args: Array[String]): Unit = {
    val rng = new DoubleSupplier() {
      override def getAsDouble(): Double = ThreadLocalRandom.current.nextDouble
    }
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val matRedDiffuse = new DiffuseMaterial(Color(0.9f, 0.1f, 0.1f))
    val matGreenDiffuse = new DiffuseMaterial(Color(0.1f, 0.9f, 0.1f))
    val matBlueDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.9f))
    val matYellowDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.1f))
    val matCyanDiffuse = new DiffuseMaterial(Color(0.1f, 0.9f, 0.9f))
    val matPinkDiffuse = new DiffuseMaterial(Color(0.9f, 0.1f, 0.9f))
    val matBlackDiffuse = new DiffuseMaterial(Color(0.1f, 0.1f, 0.1f))
    val matWhiteDiffuse = new DiffuseMaterial(Color(0.9f, 0.9f, 0.9f))
    val matGrayDiffuse = new DiffuseMaterial(Color(0.5f, 0.5f, 0.5f))
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

    val hemisphereScene = new Scene(hemisphere, closerLowCam, matAir, new DirectionalLightMaterial(Color.WHITE, 2, Vec3d(0, 1, -3).normalize, 0.5f))
    pathTracer.render(display, hemisphereScene, bounces = 12)
  }
}