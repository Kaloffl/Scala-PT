package kaloffl.spath.examples

import kaloffl.spath.Display
import kaloffl.spath.RenderEngine
import kaloffl.spath.importer.PlyImporter
import kaloffl.spath.math.Attenuation
import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.Camera
import kaloffl.spath.scene.Scene
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.ReflectiveMaterial
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode

object Dragon {

  def main(args: Array[String]): Unit = {

    val matAir = new TransparentMaterial(Color(0.2f, 0.1f, 0.05f), 0.1, 0.0, 1.0)
    val matSky = new LightMaterial(Color(1.0f, 0.95f, 0.9f) * 2, Attenuation.none)
    val matGlass = new TransparentMaterial(Color(0.5f, 0.8f, 0.8f), 7, 1.5, 1.9)
    val matRedMetal = ReflectiveMaterial(Color(0.9f, 0.1f, 0.1f), 0.0125)
    val matGreenMetal = ReflectiveMaterial(Color(0.1f, 0.9f, 0.1f), 0.0125)
    val matBlueMetal = ReflectiveMaterial(Color(0.1f, 0.1f, 0.9f), 0.0125)
    val matCyanMetal = ReflectiveMaterial(Color(0.1f, 0.8f, 0.8f), 0.0125)
    val matMagentaMetal = ReflectiveMaterial(Color(0.8f, 0.1f, 0.8f), 0.0125)
    val matYellowMetal = ReflectiveMaterial(Color(0.85f, 0.7f, 0.1f), 0.00001)
    val matFloor = DiffuseMaterial(Color(0.6f, 0.65f, 0.7f))

    val dragon = SceneNode(
      PlyImporter.load("D:/temp/dragon.ply", Vec3d(40), Vec3d(0.5, -2, 0)),
      matYellowMetal)

    val floor = SceneNode(AABB(Vec3d(0, -0.5, 0), Vec3d(16, 1, 16)), matFloor)

    val objects = SceneNode(Array(
      dragon,
      floor,
      SceneNode(new Sphere(Vec3d(0, 1.1, -4), 1.1f), matRedMetal),
      SceneNode(new Sphere(Vec3d(6, 1, 0), 1), matGreenMetal),
      SceneNode(new Sphere(Vec3d(-5.9, 0.9, -0.1), 0.9f), matBlueMetal),
      SceneNode(new Sphere(Vec3d(3, 0.9, -3), 0.9f), matCyanMetal),
      SceneNode(new Sphere(Vec3d(-3, 1, -2.8), 1), matMagentaMetal),

      SceneNode(new Sphere(Vec3d(0, 1.1, 4), 1.1f), matRedMetal),
      SceneNode(new Sphere(Vec3d(3.2, 0.8, 3), 0.8f), matCyanMetal),
      SceneNode(new Sphere(Vec3d(-3, 1, 2.8), 1), matMagentaMetal)))

    val camPos = Vec3d(-1, 6, 8)
    val dragonHead = Vec3d(-1, 4, 0)
    val dragonForward = dragonHead - camPos
    val dragonSide = Vec3d(dragonForward.z, 0, -dragonForward.x)
    val dragonTop = dragonForward.cross(dragonSide.normalize).normalize

    RenderEngine.render(
      bounces = 12,
      target = new Display(1280, 720),
      scene = new Scene(
        airMedium = matAir,
        skyMaterial = matSky,
        root = objects,
        camera = new Camera(
          position = camPos,
          forward = dragonForward.normalize,
          up = dragonTop,
          aperture = 0.1,
          focalLength = dragonForward.length)))
  }
}