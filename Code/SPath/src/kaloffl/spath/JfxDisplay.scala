package kaloffl.spath

import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import javafx.stage.WindowEvent
import kaloffl.spath.math.Color
import java.util.concurrent.locks.LockSupport
import java.util.LinkedList
import kaloffl.spath.InputEvent.Key
import java.util.HashMap

class JfxDisplay(
    override val width: Int,
    override val height: Int) extends RenderTarget {

  override def setPixel(x: Int, y: Int, color: Color) {
    ActualDisplay.instance.backBuffer.getPixelWriter.setArgb(x, y, color.toInt)
  }

  override def commit {
    Platform.runLater(new Runnable {
      override def run {
        ActualDisplay.instance.view.setImage(ActualDisplay.instance.backBuffer)
        val temp = ActualDisplay.instance.image
        ActualDisplay.instance.image = ActualDisplay.instance.backBuffer
        ActualDisplay.instance.backBuffer = temp
      }
    })
  }
  
  val jfxKeyMap = {
    val map = new HashMap[KeyCode, Key]
    map.put(KeyCode.A, InputEvent.Key_A)
    map.put(KeyCode.B, InputEvent.Key_B)
    map.put(KeyCode.C, InputEvent.Key_C)
    map.put(KeyCode.D, InputEvent.Key_D)
    map.put(KeyCode.E, InputEvent.Key_E)
    map.put(KeyCode.F, InputEvent.Key_F)
    map.put(KeyCode.G, InputEvent.Key_G)
    map.put(KeyCode.H, InputEvent.Key_H)
    map.put(KeyCode.I, InputEvent.Key_I)
    map.put(KeyCode.J, InputEvent.Key_J)
    map.put(KeyCode.K, InputEvent.Key_K)
    map.put(KeyCode.L, InputEvent.Key_L)
    map.put(KeyCode.M, InputEvent.Key_M)
    map.put(KeyCode.N, InputEvent.Key_N)
    map.put(KeyCode.O, InputEvent.Key_O)
    map.put(KeyCode.P, InputEvent.Key_P)
    map.put(KeyCode.Q, InputEvent.Key_Q)
    map.put(KeyCode.R, InputEvent.Key_R)
    map.put(KeyCode.S, InputEvent.Key_S)
    map.put(KeyCode.T, InputEvent.Key_T)
    map.put(KeyCode.U, InputEvent.Key_U)
    map.put(KeyCode.V, InputEvent.Key_V)
    map.put(KeyCode.W, InputEvent.Key_W)
    map.put(KeyCode.X, InputEvent.Key_X)
    map.put(KeyCode.Y, InputEvent.Key_Y)
    map.put(KeyCode.Z, InputEvent.Key_Z)
    // TODO map more keys
    map
  }
  val events = new Iterator[InputEvent] {
    override def hasNext: Boolean = !ActualDisplay.instance.events.isEmpty
    override def next: InputEvent = {
      val jfxEvent = ActualDisplay.instance.events.poll.asInstanceOf[KeyEvent]
      return new InputEvent(jfxKeyMap.get(jfxEvent.getCode), jfxEvent.getEventType == KeyEvent.KEY_PRESSED)
    }
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
  val events = new LinkedList[javafx.scene.input.InputEvent]

  override def start(stage: Stage): Unit = {
    val content = new BorderPane
    content.setCenter(view)
    val scene = new Scene(content, ActualDisplay.width, ActualDisplay.height)
    scene.setOnKeyPressed(new EventHandler[KeyEvent] {
      override def handle(ev: KeyEvent): Unit = events.add(ev)
    })
    stage.setScene(scene)
    stage.setResizable(false)
    stage.setOnCloseRequest(new EventHandler[WindowEvent] {
      override def handle(ev: WindowEvent): Unit = {
        System.exit(0)
      }
    })
    stage.show()
    ActualDisplay.instance = this
  }
}