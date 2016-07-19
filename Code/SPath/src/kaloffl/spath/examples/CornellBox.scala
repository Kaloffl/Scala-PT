package kaloffl.spath.examples

import kaloffl.spath.math.{Color, Vec3d}
import kaloffl.spath.scene.hints.GlobalHint
import kaloffl.spath.scene.materials.{DiffuseMaterial, EmittingMaterial}
import kaloffl.spath.scene.shapes.{AABB, Sphere, Triangle}
import kaloffl.spath.scene.structure.SceneNode
import kaloffl.spath.scene.{PinholeCamera, Scene, Viewpoint}
import kaloffl.spath.tracing.RecursivePathTracer
import kaloffl.spath.{JfxDisplay, RenderEngine}

object CornellBox {

  def main(args: Array[String]): Unit = {

    val cmatWhite = DiffuseMaterial(new Color(0.737f, 0.728f, 0.767f))
    val cmatRed = DiffuseMaterial(new Color(0.642f, 0.063f, 0.061f))
    val cmatGreen = DiffuseMaterial(new Color(0.159f, 0.373f, 0.101f))
    val cmatLight = new EmittingMaterial(new Color(34.0f, 23.6f, 8.0f), 1)

    val roomWidth = 552.5
    val roomHeight = 548.8
    val roomDepth = 559.2

    val s000 = Vec3d(130, 0, 65)
    val s001 = Vec3d(82, 0, 225)
    val s010 = Vec3d(130, 165, 65)
    val s011 = Vec3d(82, 165, 225)
    val s100 = Vec3d(290, 0, 114)
    val s101 = Vec3d(240, 0, 272)
    val s110 = Vec3d(290, 165, 114)
    val s111 = Vec3d(240, 165, 272)

    val t000 = Vec3d(265, 0, 296)
    val t001 = Vec3d(314, 0, 456)
    val t010 = Vec3d(265, 330, 296)
    val t011 = Vec3d(314, 330, 456)
    val t100 = Vec3d(423, 0, 247)
    val t101 = Vec3d(472, 0, 406)
    val t110 = Vec3d(423, 330, 247)
    val t111 = Vec3d(472, 330, 406)

    val light = SceneNode(
      new AABB(Vec3d(213, roomHeight - 0.01, 227), Vec3d(343, roomHeight + 0.99, 332)),
      cmatLight)

    val lightHint = new Sphere(Vec3d(278, roomHeight, 279), 65)

    val cornellBox = SceneNode(Array(
      light,
      SceneNode(Array(
        new Triangle(s111, s110, s010),
        new Triangle(s011, s111, s010),
        new Triangle(s011, s010, s000),
        new Triangle(s001, s011, s000),
        new Triangle(s101, s110, s111),
        new Triangle(s100, s110, s101),
        new Triangle(s000, s010, s110),
        new Triangle(s000, s110, s100),
        new Triangle(s111, s011, s001),
        new Triangle(s101, s111, s001)),
        cmatWhite),

      SceneNode(Array(
        new Triangle(t111, t110, t010),
        new Triangle(t011, t111, t010),
        new Triangle(t011, t010, t000),
        new Triangle(t001, t011, t000),
        new Triangle(t101, t110, t111),
        new Triangle(t100, t110, t101),
        new Triangle(t000, t010, t110),
        new Triangle(t000, t110, t100),
        new Triangle(t111, t011, t001),
        new Triangle(t101, t111, t001)),
        cmatWhite),

      SceneNode(
        new AABB(Vec3d(0, -1, 0), Vec3d(roomWidth, 0, roomDepth)),
        cmatWhite),
      SceneNode(
        new AABB(Vec3d(0, roomHeight, 0), Vec3d(roomWidth, roomHeight + 1, roomDepth)),
        cmatWhite),
      SceneNode(
        new AABB(Vec3d(0, 0, roomDepth), Vec3d(roomWidth, roomHeight, roomDepth + 1)),
        cmatWhite),
      SceneNode(
        new AABB(Vec3d(-1, 0, 0), Vec3d(0, roomHeight, roomDepth)),
        cmatGreen),
      SceneNode(
        new AABB(Vec3d(roomWidth, 0, 0), Vec3d(roomWidth + 1, roomHeight, roomDepth)),
        cmatRed)))

    RenderEngine.render(
      bounces = 8,
      target = new JfxDisplay(512, 512),
      tracer = RecursivePathTracer,
      view = new Viewpoint(
        position = Vec3d(278, 273, -800),
        forward = Vec3d.Front,
        up = Vec3d.Up),
      scene = new Scene(
        root = cornellBox,
        lightHints = Array(GlobalHint(lightHint)),
        camera = new PinholeCamera(sensorDistance = 0.014f)))
  }
}