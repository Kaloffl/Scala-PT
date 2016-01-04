package kaloffl.spath

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.PixelReader
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.stage.Stage

class JfxDisplay(
    override val width: Int,
    override val height: Int) extends RenderTarget {

  override def setPixel(x: Int, y: Int, color: Int) = {
    ActualDisplay.instance.backBuffer.getPixelWriter.setArgb(x, y, color)
  }

  override def update = {
    Platform.runLater(new Runnable{
      override def run {
        ActualDisplay.instance.view.setImage(ActualDisplay.instance.backBuffer)
        val temp = ActualDisplay.instance.image
        ActualDisplay.instance.image = ActualDisplay.instance.backBuffer
        ActualDisplay.instance.backBuffer = temp
      }
    })
  }

  ActualDisplay.width = width
  ActualDisplay.height = height
  
  new Thread(new Runnable {
    override def run {
      Application.launch(classOf[ActualDisplay])
    }

  }).start
  while (null == ActualDisplay.instance) {
    Thread.sleep(100)
  }
}

object ActualDisplay {
  var width = 0
  var height = 0
  var instance: ActualDisplay = null
}

class ActualDisplay extends Application {
  
  var image = new WritableImage(ActualDisplay.width, ActualDisplay.height)
  var backBuffer = new WritableImage(ActualDisplay.width, ActualDisplay.height)
  val view = new ImageView
  
  override def start(stage: Stage): Unit = {
    val content = new BorderPane
    content.setCenter(view)
    val scene = new Scene(content, ActualDisplay.width, ActualDisplay.height)
    stage.setScene(scene)
    stage.setResizable(false)
    stage.show()
    ActualDisplay.instance = this
  }
}