package kaloffl.spath.test

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.Node
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.stage.Stage
import javafx.scene.chart.XYChart
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.shape.Sphere
import kaloffl.spath.math.Vec3d
import java.util.concurrent.ThreadLocalRandom
import javafx.scene.paint.PhongMaterial
import javafx.scene.paint.Color

/**
 * This class was created to test the even distribution of points by the
 * randomHemisphere operation on the Vec3d class. The distribution used to
 * be denser around the equator compared to the pole. That was fixed thanks to
 * this class.
 *
 * @author Lars Donner
 */
class HemisphereDistribution extends Application {

  override def start(primaryStage: Stage): Unit = {

    val gridPane = new GridPane

    gridPane.add(createChart(Vec3d.UP, 0, 0), 0, 0)
    gridPane.add(createChart(Vec3d.DOWN, 0, 1), 0, 1)
    gridPane.add(createChart(Vec3d.LEFT, 1, 0), 1, 0)
    gridPane.add(createChart(Vec3d.RIGHT, 1, 1), 1, 1)
    gridPane.add(createChart(Vec3d.FRONT, 2, 0), 2, 0)
    gridPane.add(createChart(Vec3d.BACK, 2, 1), 2, 1)

    val scene = new Scene(gridPane, 900, 600)
    primaryStage.setScene(scene)
    primaryStage.show
  }

  def createChart(startVector: Vec3d, x: Int, y: Int): Node = {
    val rnd = () ⇒ ThreadLocalRandom.current.nextFloat

    val pane = new Pane()

    val mat = new PhongMaterial(
      new Color(
        0.5 + 0.5 * y,
        0.5 + 0.5 * (x % 2),
        0.5 + 0.5 * (x / 2),
        1.0))

    val size = Vec3d(150, 150, 150)
    for (i ← 0 until 10000) {
      val vec = startVector.randomHemisphere(rnd) * size + size
      val sphere = new Sphere(1)
      sphere.setMaterial(mat)
      sphere.setTranslateX(vec.x + (x * 300))
      sphere.setTranslateY(vec.y + (y * 300))
      sphere.setTranslateZ(vec.z)
      pane.getChildren.add(sphere)
    }

    return pane
  }
}

object HemisphereDistribution {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[HemisphereDistribution], args: _*)
  }
}