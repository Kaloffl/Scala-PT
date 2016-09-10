package kaloffl.spath.tracing

import kaloffl.spath.scene.materials.Material

/**
  * Utility class for keeping track of participating media encountered during path tracing.
  */
class MediaStack(size: Int, initialValues: Array[Material]) {

  var mediaIndex = initialValues.length - 1
  val media = new Array[Material](size)
  System.arraycopy(initialValues, 0, media, 0, initialValues.length)

  def add(material: Material): Unit = {
    mediaIndex += 1
    media(mediaIndex) = material
  }

  def remove(material: Material): Unit = {
    var i = mediaIndex
    while (i > 0) {
      if (media(i) == material) {
        var j = i + 1
        while (j <= mediaIndex) {
          media(j - 1) = media(j)
          j += 1
        }
        i = 0
      }
      i -= 1
    }
    mediaIndex = Math.max(0, mediaIndex - 1)
  }

  def head: Material = media(mediaIndex)
}
