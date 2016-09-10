package kaloffl.spath.tracing

import kaloffl.spath.math.Color

class SampleStorage(val width: Int, val height: Int) {

  var sampleCounts = new Array[Int](width * height)
  val samples = new Array[Float](width * height * 3)

  def addSample(x: Int, y: Int, color: Color): Unit = {
    val index = x * height + y
    sampleCounts(index) += 1
    if(sampleCounts(index) < 0) {
      System.err.println("integer overflow in sample storage!")
    }
    samples(index * 3) += color.r2
    samples(index * 3 + 1) += color.g2
    samples(index * 3 + 2) += color.b2
  }

  def getColor(x: Int, y: Int): Color = {
    val index = x * height + y
    val sampleCount = sampleCounts(index)
    if(0 == sampleCount) {
      Color.Black
    } else {
      new Color(
    			samples(index * 3) / sampleCount,
    			samples(index * 3 + 1) / sampleCount,
    			samples(index * 3 + 2) / sampleCount)
    }
  }
}