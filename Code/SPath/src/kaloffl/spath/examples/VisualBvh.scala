package kaloffl.spath.examples

import kaloffl.spath.Display
import kaloffl.spath.RenderEngine
import kaloffl.spath.bvh.BvhBuilder
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
import kaloffl.spath.scene.shapes.Sphere
import kaloffl.spath.scene.structure.SceneNode

object VisualBvh {

  def main(args: Array[String]): Unit = {

    val matAir = new TransparentMaterial(
      color = Color.Black,
      scatterProbability = 0.0625)
    val matSky = new LightMaterial(Color(1.0f, 0.95f, 0.9f) * 2, Attenuation.none)
    val matGlassRed = new TransparentMaterial(Color(0.7f, 4, 4), 1, 20, 1.7)
    val matGlassGreen = new TransparentMaterial(Color(4, 0.7f, 4), 1, 20, 1.7)
    val matGlassBlue = new TransparentMaterial(Color(4, 4, 0.7f), 1, 20, 1.7)
    val matFloor = DiffuseMaterial(Color(0.7f, 0.75f, 0.9f))

    val bunny = BvhBuilder.buildTree(
      PlyImporter.load(
        file = "D:/temp/bunny_flipped.ply",
        scale = Vec3d(40),
        offset = Vec3d(0, -0.659748 * 2, 0)))

    val boxRed = SceneNode(bunny.hull, matGlassRed)
    val boxGreen = bunny.children.map { n ⇒ SceneNode(n.hull, matGlassGreen) }
    val boxBlue = bunny.children.flatMap { _.children }.map { n ⇒ SceneNode(n.hull, matGlassBlue) }

    val boxes = SceneNode((0 until 5).foldLeft(bunny.children) { (ns, i) ⇒
      ns.flatMap { _.children }
    }.map(_.hull), matFloor)

    val floor = SceneNode(AABB(Vec3d(0, -0.07, 0), Vec3d(8, 0.1, 8)), matFloor)

    val light = SceneNode(new Sphere(Vec3d(-3, 5.5, 0.4), 1f), matSky)

    val bunnyForward = Vec3d(1, 0, 0)
    val bunnyTop = bunnyForward.cross(Vec3d.Front).normalize

    RenderEngine.render(
      bounces = 12,
      target = new Display(1280, 720),
      scene = new Scene(
        root = SceneNode(Array(boxes, light)),
        airMedium = matAir,
        skyMaterial = matFloor,
        camera = new Camera(
          position = Vec3d(0.5, 2.5, 0.4),
          forward = bunnyForward.normalize,
          up = bunnyTop,
          aperture = 0.0,
          focalLength = bunnyForward.length)))
  }
}