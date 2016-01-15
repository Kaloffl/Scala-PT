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
import kaloffl.spath.math.Vec3d
import java.util.function.DoubleSupplier
import java.util.concurrent.ThreadLocalRandom
import javafx.scene.chart.LineChart
import kaloffl.spath.math.Mat3d
import kaloffl.spath.math.Vec2d

class ImportanceDotDistribution extends Application {

  override def start(primaryStage: Stage): Unit = {
    val pane = new Pane

    val xAxis = new NumberAxis
    val yAxis = new NumberAxis
    val chart = new LineChart(xAxis, yAxis)
    chart.setLegendVisible(false)
    pane.getChildren.addAll(chart)

    val rngSeries = new Series[Number, Number]
    for (i ← 0 until 100) rngSeries.getData.add(new Data(i, 0))
    chart.getData.add(rngSeries)

    val referenceSeries = new Series[Number, Number]
    for (i ← 0 until 100) referenceSeries.getData.add(new Data(i, 0))
    chart.getData.add(referenceSeries)

    val normalSeries = new Series[Number, Number]
    for (i ← 0 until 100) normalSeries.getData.add(new Data(i, 0))
    chart.getData.add(normalSeries)

    val weighted1Series = new Series[Number, Number]
    for (i ← 0 until 100) weighted1Series.getData.add(new Data(i, 0))
    chart.getData.add(weighted1Series)

    val scene = new Scene(pane, 500, 400)
    primaryStage.setScene(scene)
    primaryStage.show

    val random = new DoubleSupplier {
      override def getAsDouble: Double = ThreadLocalRandom.current().nextDouble
    }

    def addToSeries(series: Series[Number, Number], num: Double): Unit = {
      val bucket = (num * 100).toInt
      val data = series.getData.get(bucket)
      data.setYValue(data.getYValue.intValue + 1)
    }

    new AnimationTimer() {
      override def handle(l: Long) {
        addToSeries(rngSeries, random.getAsDouble)
        //        while (true) {
        val newDir = Vec3d.Up.randomHemisphere(Vec2d.random(random))
        val diffuse = newDir.dot(Vec3d.Up)
        addToSeries(normalSeries, diffuse)

        if (diffuse > random.getAsDouble) {
          addToSeries(referenceSeries, diffuse)
          addToSeries(weighted1Series, Vec3d.Up.weightedHemisphere(Vec2d.random(random)).dot(Vec3d.Up))
        }
        //        }
      }
    }.start
  }
}

object ImportanceDotDistribution {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[ImportanceDotDistribution], args: _*)
  }
}