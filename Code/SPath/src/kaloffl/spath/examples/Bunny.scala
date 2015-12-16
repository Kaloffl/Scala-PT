package kaloffl.spath.examples

import kaloffl.spath.scene.Scene
import kaloffl.spath.importer.PlyImporter
import kaloffl.spath.Display
import kaloffl.spath.PathTracer
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.Camera
import kaloffl.spath.math.Vec3d
import kaloffl.spath.math.Color
import kaloffl.spath.scene.materials.LightMaterial
import kaloffl.spath.scene.materials.DiffuseMaterial
import kaloffl.spath.scene.materials.MaskedMaterial
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.materials.TransparentMaterial
import kaloffl.spath.scene.materials.CheckeredMask
import kaloffl.spath.scene.structure.FlatObject
import kaloffl.spath.bvh.Bvh
import kaloffl.spath.scene.structure.HierarchicalObject
import kaloffl.spath.math.Attenuation
import kaloffl.spath.scene.shapes.Sphere

object Bunny {

  def main(args: Array[String]): Unit = {
    val display = new Display(1280, 720)
    val pathTracer = new PathTracer

    val matAir = new TransparentMaterial(Color(0.2f, 0.1f, 0.05f), 0.1, 0.0, 1.0)
    val matSky = new LightMaterial(Color(1.0f, 0.95f, 0.9f), 2, Attenuation.none)
    val matGlass = new TransparentMaterial(Color(0.9f, 0.9f, 0.9f), 1, 0, 1.7)
    val matFloor = new DiffuseMaterial(Color(0.6f, 0.65f, 0.7f))
    
    val bunny = SceneNode(
        PlyImporter.load("D:/temp/bunny_flipped.ply", Vec3d(40), Vec3d(0, -0.659748 * 2, 0)),
        matGlass)
        
    val floor = SceneNode(AABB(Vec3d(-1, -0.05, -1), Vec3d(8, 0.1, 8)), matFloor)

    val bunnyForward = Vec3d(-2, -1.75, -10)
    val bunnyTop = bunnyForward.cross(Vec3d.RIGHT).normalize
    val bunnyCam = new Camera(Vec3d(0, 4.5, 9), bunnyForward.normalize, bunnyTop, 0.2, bunnyForward.length)

    val bunnyScene = new Scene(SceneNode(Array(floor, bunny)), bunnyCam, matAir, matSky)

    pathTracer.render(display, bunnyScene, bounces = 12)
  }
}