package kaloffl.spath.test

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.stage.Stage
import javafx.scene.chart.XYChart
import javafx.scene.layout.GridPane
import kaloffl.spath.math.Vec3d
import java.util.concurrent.ThreadLocalRandom

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

    gridPane.add(createChart(Vec3d.UP, "Up"), 0, 0)
    gridPane.add(createChart(Vec3d.DOWN, "Down"), 0, 1)
    gridPane.add(createChart(Vec3d.LEFT, "Left"), 1, 0)
    gridPane.add(createChart(Vec3d.RIGHT, "Right"), 1, 1)
    gridPane.add(createChart(Vec3d.FRONT, "Front"), 2, 0)
    gridPane.add(createChart(Vec3d.BACK, "Back"), 2, 1)

    val scene = new Scene(gridPane, 800, 600)
    primaryStage.setScene(scene)
    primaryStage.show
  }

  def createChart(startVector: Vec3d, name: String): XYChart[_, _] = {
    val rnd = () ⇒ ThreadLocalRandom.current.nextFloat
    val xAxis = new NumberAxis
    val yAxis = new NumberAxis
    val chart = new LineChart[Number, Number](xAxis, yAxis)
    val series = new XYChart.Series[Number, Number]
    chart.getData.add(series)
    chart.setLegendVisible(false)
    chart.setTitle(name)

    val vectors = for (i ← 0 until 10000) yield startVector.randomHemisphere(rnd)
    val distances = for (v ← vectors) yield Math.sin((v - startVector).length)
    val rounded = distances.map { x ⇒ Math.round(x * 100) }
    rounded.distinct.sorted.map { x ⇒ (x, rounded.count { y ⇒ x == y }) }.foreach { pair ⇒
      series.getData.add(new XYChart.Data[Number, Number](pair._1 / 100.0, pair._2))
    }
    return chart
  }
}

object HemisphereDistribution {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[HemisphereDistribution], args: _*)
  }
}