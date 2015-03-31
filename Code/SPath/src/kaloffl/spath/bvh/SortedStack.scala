package kaloffl.spath.bvh

/**
 * @author Lars
 */
class SortedStack[T](lt: (T, T) ⇒ Boolean) {

  private var sizeCount = 0
  private var head: StackNode[T] = null

  def add(value: T): Int = {
    sizeCount += 1

    if (null == head) {
      head = new StackNode(value, null)
      return 0
    }

    var lastNode: StackNode[T] = null
    var currentNode = head
    var index = 0
    while (null != currentNode && lt(currentNode.value, value)) {
      lastNode = currentNode
      currentNode = currentNode.next
      index += 1
    }
    val newNode = new StackNode(value, currentNode)
    if (null == lastNode) {
      head = newNode
    } else {
      lastNode.next = newNode
    }

    return index
  }

  def pop(): T = {
    if (0 == size) throw new RuntimeException("Can't pop from empty stack.")

    sizeCount -= 1
    val value = head.value
    head = head.next
    return value
  }

  def peek: T = {
    if (0 == size) throw new RuntimeException("Can't peek in empty stack.")

    return head.value
  }

  def clear: Unit = {
    sizeCount = 0
    head = null
  }

  def size: Int = { sizeCount }

  def empty: Boolean = { 0 == size }

  class StackNode[T](val value: T, var next: StackNode[T])
}
