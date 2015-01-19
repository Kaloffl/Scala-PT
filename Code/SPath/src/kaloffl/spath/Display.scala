package kaloffl.spath

import javax.swing.JFrame
import java.awt.Canvas
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.awt.Dimension
import javax.swing.WindowConstants

class Display(val width: Int, val height: Int) {

  private val window = new JFrame("Scala Path Tracer")
  private val canvas = new Canvas()
  private val buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
  private val raster = (buffer.getRaster.getDataBuffer.asInstanceOf[DataBufferInt]).getData();

  window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  window.setResizable(false)
  canvas.setPreferredSize(new Dimension(width, height))
  window.add(canvas)
  window.pack()
  window.setVisible(true)

  canvas.createBufferStrategy(2)

  private val strategy = canvas.getBufferStrategy

  def redraw: Unit = {
    val graphicsContext = strategy.getDrawGraphics

    graphicsContext.drawImage(buffer, 0, 0, null)

    graphicsContext.dispose
    strategy.show
  }

  def drawPixel(x: Int, y: Int, color: Int): Unit = {
    raster(x + y * width) = color
  }
}