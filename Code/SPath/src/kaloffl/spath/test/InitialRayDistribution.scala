package kaloffl.spath.test

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.ScatterChart
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.layout.Pane
import javafx.stage.Stage

class InitialRayDistribution extends Application {

  override def start(primaryStage: Stage): Unit = {
    val pane = new Pane

    val xAxis = new NumberAxis(-1.1, 1.1, 0.1)
    val yAxis = new NumberAxis(-1.1, 1.1, 0.1)
    val chart = new ScatterChart(xAxis, yAxis)
    chart.setLegendVisible(false)
    pane.getChildren.addAll(chart)

    def sampleToDistribution(s: Int): Double = {
      if (0 == s) return 1
      val n = s / 2 + 2 * (Math.log(s / 2 + 2) / Math.log(2) - 1).toInt
      val p = Math.pow(2, (Math.log(n + 1) / Math.log(2)).toInt)
      return (n + 1 - p) / (p + 1) * (2 * (s & 0x01) - 1)
    }

    val series = new Series[Number, Number]
    chart.getData.add(series)

    val scene = new Scene(pane, 500, 400)
    primaryStage.setScene(scene)
    primaryStage.show

    new AnimationTimer() {
      var i = 0
      override def handle(l: Long) {
        val r = Math.sqrt(i).toInt
        val dx = sampleToDistribution(Math.min(r, i - r * r))
        val dy = sampleToDistribution(Math.min(r, r * r + 2 * r - i))
        series.getData.add(new Data[Number, Number](dx, dy))
        i += 1
      }
    }.start
  }
}

object InitialRayDistribution {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[InitialRayDistribution], args: _*)
  }
}