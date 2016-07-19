package kaloffl.spath

import java.util.{HashMap, LinkedList}
import javafx.application.{Application, Platform}
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.image.{ImageView, WritableImage}
import javafx.scene.input.{Clipboard, ClipboardContent, KeyCode, MouseButton}
import javafx.scene.layout.BorderPane
import javafx.stage.{Stage, WindowEvent}

import kaloffl.spath.KeyEvent.Key
import kaloffl.spath.math.Color

class JfxDisplay(
    override val width: Int,
    override val height: Int) extends RenderTarget {

  override def setPixel(x: Int, y: Int, color: Color) {
    ActualDisplay.instance.backBuffer.getPixelWriter.setArgb(x, y, color.toInt)
  }

  override def commit(): Unit = {
    Platform.runLater(new Runnable {
      override def run(): Unit = {
        ActualDisplay.instance.view.setImage(ActualDisplay.instance.backBuffer)
        val temp = ActualDisplay.instance.image
        ActualDisplay.instance.image = ActualDisplay.instance.backBuffer
        ActualDisplay.instance.backBuffer = temp
      }
    })
  }

  val events = new Iterator[InputEvent] {
    override def hasNext: Boolean = !ActualDisplay.instance.events.isEmpty
    override def next: InputEvent = ActualDisplay.instance.events.poll
  }

  ActualDisplay.width = width
  ActualDisplay.height = height

  new Thread(new Runnable {
    override def run(): Unit = {
      Application.launch(classOf[ActualDisplay])
    }

  }).start()
  while (null == ActualDisplay.instance) {
    Thread.sleep(100)
  }
}

object ActualDisplay {
  var width = 0
  var height = 0
  var instance: ActualDisplay = _

  val jfxKeyMap = {
    val map = new HashMap[KeyCode, Key]
    map.put(KeyCode.A, KeyEvent.Key_A)
    map.put(KeyCode.B, KeyEvent.Key_B)
    map.put(KeyCode.C, KeyEvent.Key_C)
    map.put(KeyCode.D, KeyEvent.Key_D)
    map.put(KeyCode.E, KeyEvent.Key_E)
    map.put(KeyCode.F, KeyEvent.Key_F)
    map.put(KeyCode.G, KeyEvent.Key_G)
    map.put(KeyCode.H, KeyEvent.Key_H)
    map.put(KeyCode.I, KeyEvent.Key_I)
    map.put(KeyCode.J, KeyEvent.Key_J)
    map.put(KeyCode.K, KeyEvent.Key_K)
    map.put(KeyCode.L, KeyEvent.Key_L)
    map.put(KeyCode.M, KeyEvent.Key_M)
    map.put(KeyCode.N, KeyEvent.Key_N)
    map.put(KeyCode.O, KeyEvent.Key_O)
    map.put(KeyCode.P, KeyEvent.Key_P)
    map.put(KeyCode.Q, KeyEvent.Key_Q)
    map.put(KeyCode.R, KeyEvent.Key_R)
    map.put(KeyCode.S, KeyEvent.Key_S)
    map.put(KeyCode.T, KeyEvent.Key_T)
    map.put(KeyCode.U, KeyEvent.Key_U)
    map.put(KeyCode.V, KeyEvent.Key_V)
    map.put(KeyCode.W, KeyEvent.Key_W)
    map.put(KeyCode.X, KeyEvent.Key_X)
    map.put(KeyCode.Y, KeyEvent.Key_Y)
    map.put(KeyCode.Z, KeyEvent.Key_Z)
    map.put(KeyCode.DIGIT0, KeyEvent.Key_0)
    map.put(KeyCode.DIGIT1, KeyEvent.Key_1)
    map.put(KeyCode.DIGIT2, KeyEvent.Key_2)
    map.put(KeyCode.DIGIT3, KeyEvent.Key_3)
    map.put(KeyCode.DIGIT4, KeyEvent.Key_4)
    map.put(KeyCode.DIGIT5, KeyEvent.Key_5)
    map.put(KeyCode.DIGIT6, KeyEvent.Key_6)
    map.put(KeyCode.DIGIT7, KeyEvent.Key_7)
    map.put(KeyCode.DIGIT8, KeyEvent.Key_8)
    map.put(KeyCode.DIGIT9, KeyEvent.Key_9)
    map.put(KeyCode.F1, KeyEvent.Key_F1)
    map.put(KeyCode.F2, KeyEvent.Key_F2)
    map.put(KeyCode.F3, KeyEvent.Key_F3)
    map.put(KeyCode.F4, KeyEvent.Key_F4)
    map.put(KeyCode.F5, KeyEvent.Key_F5)
    map.put(KeyCode.F6, KeyEvent.Key_F6)
    map.put(KeyCode.F7, KeyEvent.Key_F7)
    map.put(KeyCode.F8, KeyEvent.Key_F8)
    map.put(KeyCode.F9, KeyEvent.Key_F9)
    map.put(KeyCode.F10, KeyEvent.Key_F10)
    map.put(KeyCode.F11, KeyEvent.Key_F11)
    map.put(KeyCode.F12, KeyEvent.Key_F12)
    map.put(KeyCode.TAB, KeyEvent.Key_Tab)
    map.put(KeyCode.SPACE, KeyEvent.Key_Space)
    map.put(KeyCode.BACK_SPACE, KeyEvent.Key_Backspace)
    map.put(KeyCode.ENTER, KeyEvent.Key_Enter)
    map.put(KeyCode.SHIFT, KeyEvent.Key_Shift)
    map.put(KeyCode.CONTROL, KeyEvent.Key_Control)
    map.put(KeyCode.ALT, KeyEvent.Key_Alt)
    map.put(KeyCode.ESCAPE, KeyEvent.Key_Escape)
    map.put(KeyCode.UP, KeyEvent.Key_Up)
    map.put(KeyCode.DOWN, KeyEvent.Key_Down)
    map.put(KeyCode.LEFT, KeyEvent.Key_Left)
    map.put(KeyCode.RIGHT, KeyEvent.Key_Right)
    map
  }
}

class ActualDisplay extends Application {

  var image = new WritableImage(ActualDisplay.width, ActualDisplay.height)
  var backBuffer = new WritableImage(ActualDisplay.width, ActualDisplay.height)
  val view = new ImageView
  val events = new LinkedList[InputEvent]

  override def start(stage: Stage): Unit = {
    val content = new BorderPane
    content.setCenter(view)
    val scene = new Scene(content, ActualDisplay.width, ActualDisplay.height)
    scene.setOnKeyPressed(new EventHandler[javafx.scene.input.KeyEvent] {
      override def handle(ev: javafx.scene.input.KeyEvent): Unit = {
        val key = ActualDisplay.jfxKeyMap.get(ev.getCode)
        if (null != key) {
          events.add(new KeyEvent(key, true))
        }
        if (ev.isControlDown && ev.getCode == KeyCode.C) {
          val content = new ClipboardContent
          content.putImage(image)
          Clipboard.getSystemClipboard.setContent(content)
        }
      }
    })
    scene.setOnKeyReleased(new EventHandler[javafx.scene.input.KeyEvent] {
      override def handle(ev: javafx.scene.input.KeyEvent): Unit = {
        val key = ActualDisplay.jfxKeyMap.get(ev.getCode)
        if (null != key) {
          events.add(new KeyEvent(key, false))
        }
        if (ev.isControlDown && ev.getCode == KeyCode.C) {
          val content = new ClipboardContent
          content.putImage(image)
          Clipboard.getSystemClipboard.setContent(content)
        }
      }
    })
    scene.setOnMousePressed(new EventHandler[javafx.scene.input.MouseEvent] {
      override def handle(ev: javafx.scene.input.MouseEvent): Unit = {
        val key = ev.getButton match {
          case MouseButton.PRIMARY => KeyEvent.Mouse_1
          case MouseButton.SECONDARY => KeyEvent.Mouse_2
          case MouseButton.MIDDLE => KeyEvent.Mouse_3
          case _ => null
        }
        events.add(new KeyEvent(key, true))
      }
    })
    scene.setOnMouseReleased(new EventHandler[javafx.scene.input.MouseEvent] {
      override def handle(ev: javafx.scene.input.MouseEvent): Unit = {
        val key = ev.getButton match {
          case MouseButton.PRIMARY => KeyEvent.Mouse_1
          case MouseButton.SECONDARY => KeyEvent.Mouse_2
          case MouseButton.MIDDLE => KeyEvent.Mouse_3
          case _ => null
        }
        events.add(new KeyEvent(key, false))
      }
    })
    scene.setOnMouseMoved(new EventHandler[javafx.scene.input.MouseEvent] {
      override def handle(ev: javafx.scene.input.MouseEvent): Unit = {
        events.add(MouseEvent(ev.getX.toInt, ev.getY.toInt, dragged = false))
      }
    })
    scene.setOnMouseDragged(new EventHandler[javafx.scene.input.MouseEvent] {
      override def handle(ev: javafx.scene.input.MouseEvent): Unit = {
        events.add(MouseEvent(ev.getX.toInt, ev.getY.toInt, dragged = true))
      }
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