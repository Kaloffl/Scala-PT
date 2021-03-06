package kaloffl.spath

import java.awt.{Canvas, Dimension, Graphics}
import java.awt.image.{BufferedImage, DataBufferInt}
import javax.swing.{JFrame, WindowConstants}

import kaloffl.spath.math.Color

/**
 * When instantiated, this class creates a new window with a canvas of the given
 * size.<br>
 * Individual pixels on said canvas can be modified. To display the changes,
 * redraw must be called.
 */
class AwtDisplay(override val width: Int, override val height: Int) extends RenderTarget {

  private val window = new JFrame("Scala Path Tracer")
  private val canvas = new Canvas() {
    override def paint(g: Graphics): Unit = {
      g.drawImage(buffer, 0, 0, null)
    }
  }
  private val buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
  private val raster = buffer.getRaster.getDataBuffer.asInstanceOf[DataBufferInt].getData()

  window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  window.setResizable(false)
  canvas.setPreferredSize(new Dimension(width, height))
  window.add(canvas)
  window.pack()
  window.setVisible(true)

  canvas.createBufferStrategy(2)

  private val strategy = canvas.getBufferStrategy

  /**
   * Displays the current image to the screen
   */
  override def commit(): Unit = {
    val graphicsContext = strategy.getDrawGraphics

    graphicsContext.drawImage(buffer, 0, 0, null)

    graphicsContext.dispose()
    strategy.show()
  }

  /**
   * Sets the pixel on the given position to the given color value. Does not
   * check bounds, so bad positions may cause ArrayIndexOutOfBoundsExceptions.
   */
  override def setPixel(x: Int, y: Int, color: Color): Unit = {
    raster(x + y * width) = color.toInt
  }
}