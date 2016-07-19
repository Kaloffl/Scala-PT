package kaloffl.spath.bvh

class ValuedArrayStack[T] {
  val bufferSize = 32
  var data: Array[T] = new Array[AnyRef](bufferSize).asInstanceOf[Array[T]]
  var values: Array[Double] = new Array(bufferSize)
  var start = bufferSize

  def add(element: T, value: Double): Int = {
    if (0 == start) {
      throw new RuntimeException("Stack buffer is full")
    }

    var index = start
    start -= 1
    while (index != bufferSize && values(index) < value) {
      data(index - 1) = data(index)
      values(index - 1) = values(index)
      index += 1
    }
    data(index - 1) = element
    values(index - 1) = value

    return index - 1
  }

  private def checkEmpty(): Unit = {
    if (empty) throw new RuntimeException("Cant pop from an empty stack")
  }
  
  def pop: (T, Double) = {
    checkEmpty()
    val element = data(start)
    val value = values(start)
    start += 1
    return (element, value)
  }

  def popElement: T = {
    checkEmpty()
    val element = data(start)
    start += 1
    return element
  }

  def popValue: Double = {
    checkEmpty()
    val value = values(start)
    start += 1
    return value
  }

  def peek: (T, Double) = {
    checkEmpty()
    (data(start), values(start))
  }

  def peekElement: T = {
    checkEmpty()
    data(start)
  }

  def peekValue: Double = {
    checkEmpty()
    values(start)
  }

  def clear(): Unit = {
    data = new Array[AnyRef](bufferSize).asInstanceOf[Array[T]]
    start = bufferSize
  }

  def size = bufferSize - start

  def empty = start == bufferSize
}