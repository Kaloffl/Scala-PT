package kaloffl.spath.tracing

import java.util.ArrayList

import kaloffl.spath.math.Color
import kaloffl.spath.math.Vec2d

trait SampleStorage {
  def addSample(x: Float, y: Float, color: Color): Unit
  def getColor(x: Float, y: Float): Color
}

class DiscreteSampleStorage(val width: Int, val height: Int) extends SampleStorage {

  val sampleCounts = new Array[Short](width * height)
  val samples = new Array[Float](width * height * 3)

  override def addSample(x: Float, y: Float, color: Color): Unit = {
    val index = x.toInt * height + y.toInt
    sampleCounts(index) = (sampleCounts(index) + 1).toShort
    samples(index * 3) += color.r2
    samples(index * 3 + 1) += color.g2
    samples(index * 3 + 2) += color.b2
  }

  override def getColor(x: Float, y: Float): Color = {
    val index = x.toInt * height + y.toInt
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

class InterpolationSampleStorage extends SampleStorage {
    val positions = new ArrayList[Vec2d]
    val colors = new ArrayList[Color]
  
    override def addSample(x: Float, y: Float, color: Color): Unit = {
      positions.add(Vec2d(x, y))
      colors.add(color)
    }
  
    override def getColor(x: Float, y: Float): Color = {
      val pos = Vec2d(x, y)
      val closestColors = new Array[Color](10)
      val distances = new Array[Float](10)
      var nearColor = Color.Black
      var nearCount = 0
      for (i <- 0 until positions.size) {
        val p = positions.get(i)
        if (Math.abs(p.x - x) <= 0.5f && Math.abs(p.y - y) <= 0.5f) {
          nearColor += colors.get(i)
          nearCount += 1
        } else if (0 == nearCount) {
      	  var dist = (pos - p).lengthSq.toFloat
          var color = colors.get(i)
      	  var j = 0
          while(j < distances.length) {
            if (0 == distances(j)) {
              distances(j) = dist
              closestColors(j) = color
              j = distances.length
            } else if(dist < distances(j)) {
              var tempD = dist
              dist = distances(j)
              distances(j) = tempD
              var tempC = color
              color = closestColors(j)
              closestColors(j) = tempC
            }
            j += 1
          }
        }
      }
      if(0 == nearCount) {
      	var distSum = 0f
      	var color = Color.Black
      	var i = 0
      	while(i < distances.length && null != closestColors(i)) {
      	  color += closestColors(i) / distances(i)
      	  distSum += 1 / distances(i)
      	  i += 1
      	}
      	color / distSum
      } else {
        nearColor / nearCount
      }
    }
}