package kaloffl.spath.examples

import kaloffl.spath.Display
import kaloffl.spath.PathTracer
import kaloffl.spath.importer.PlyImporter
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.structure.SceneNode

object Dragon {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val matAir = new TransparentMaterial(Color(0.2f, 0.1f, 0.05f), 0.1, 0.0, 1.0)
    val matSky = new LightMaterial(Color(1.0f, 0.95f, 0.9f), 2, Attenuation.none)
    val matGlass = new TransparentMaterial(Color(0.5f, 0.8f, 0.8f), 1, 0, 1.7)
    val matFloor = new DiffuseMaterial(Color(0.6f, 0.65f, 0.7f))

    val dragon = SceneNode(
      PlyImporter.load("D:/temp/dragon.ply", Vec3d(40), Vec3d(0.5, -2, 0)),
      matGlass)

    val floor = SceneNode(
      AABB(Vec3d(0, -0.5, 0), Vec3d(16, 1, 16)),
      matFloor)

    val dragonForward = Vec3d(5, -4, -5)
    val dragonSide = Vec3d(dragonForward.z, 0, -dragonForward.x)
    val dragonTop = dragonForward.cross(dragonSide.normalize).normalize
    val dragonCam = new Camera(Vec3d(-6, 8, 6), dragonForward.normalize, dragonTop, 0.1, dragonForward.length)

    val dragonScene = new Scene(SceneNode(Array(floor, dragon)), dragonCam, matAir, matSky)

    pathTracer.render(display, dragonScene, bounces = 12)
  }
}