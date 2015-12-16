package kaloffl.spath.bvh

class SortedArrayStack[T: Manifest](lt: (T, T) ⇒ Boolean) {
  val bufferSize = 24
  var data: Array[T] = new Array(bufferSize)
  var start = bufferSize

  def add(element: T): Int = {
    if (0 == start) {
      throw new RuntimeException("Stack buffer is full")
    }

    var index = start
    start -= 1
    while (index != bufferSize && lt(data(index), element)) {
      data(index - 1) = data(index)
      index += 1
    }
    data(index - 1) = element

    return index - 1
  }

  def pop: T = {
    if (empty) throw new RuntimeException("Cant pop from an empty stack")
    val value = data(start)
    start += 1
    return value
  }

  def peek: T = {
    if (empty) throw new RuntimeException("Cant peek in an empty stack")
    data(start)
  }

  def clear: Unit = {
    data = new Array(bufferSize)
    start = bufferSize
  }

  def size: Int = bufferSize - start

  def empty: Boolean = (start == bufferSize)
}