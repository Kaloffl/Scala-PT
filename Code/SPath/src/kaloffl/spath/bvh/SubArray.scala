package kaloffl.spath.bvh

import java.util.Arrays
import java.util.Comparator
import scala.reflect.ClassTag

/**
 * @author Lars
 */
class SubArray[T](
    val array: Array[T],
    val from: Int,
    val until: Int) {

  def slice(from: Int, until: Int): SubArray[T] = {
    new SubArray(array, this.from + from, this.from + until)
  }

  def map[R: ClassTag](f: T ⇒ R): SubArray[R] = {
    val length = this.length
    val newArray = new Array[R](length)
    var i = 0
    while (i < length) {
      newArray(i) = f(array(from + i))
      i += 1
    }
    return new SubArray(newArray, 0, length)
  }

  def toArray: Array[T] = {
    if (0 == from && until == array.length) return array
    return array.slice(from, until)
  }

  def apply(i: Int): T = array(from + i)

  def head: T = apply(0)
  def tail: SubArray[T] = slice(1, length)
  def last: T = apply(until - 1)
  def init: SubArray[T] = slice(0, length - 1)
  def length: Int = until - from
  def foldLeft[R](start: R)(f: (T, R) ⇒ R): R = {
    var r = start
    var i = 0
    val length = this.length
    while (i < length) {
      r = f(apply(i), r)
      i += 1
    }
    return r
  }

  def sort(ord: Comparator[T]): Unit = {
//    if (length > 32) {
//      Arrays.parallelSort(
//        array.asInstanceOf[Array[Object]],
//        from,
//        until,
//        ord.asInstanceOf[Comparator[Object]])
//    } else {
      Arrays.sort(
        array.asInstanceOf[Array[Object]],
        from,
        until,
        ord.asInstanceOf[Comparator[Object]])
//    }
  }
}