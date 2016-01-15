package kaloffl.spath.bvh

import java.util.Arrays
import java.util.Comparator
import scala.reflect.ClassTag

class SubArray[T](
    val array: Array[T],
    val from: Int,
    val until: Int) {

  def this(array: Array[T]) = this(array, 0, array.length)

  /**
   * Creates a new SubArray beginning at the given 'from' and ending at the 
   * given 'until'
   */
  def slice(from: Int, until: Int): SubArray[T] = {
    val nf = this.from + from
    if(nf > array.length) throw new ArrayIndexOutOfBoundsException()
    val nu = this.from + until
    if(nu > array.length) throw new ArrayIndexOutOfBoundsException()
    if(nu <= nf) throw new RuntimeException("SubArray has no or negative size")
    new SubArray(array, this.from + from, this.from + until)
  }

  /**
   * Returns a new SubArray beginning one index after the given i and going
   * until the same end as the parent
   */
  def after(i: Int) = slice(i + 1, length)

  /**
   * Returns a new SubArray beginning at the given i and going until the same
   * end as the parent
   */
  def startingAt(i: Int) = slice(i, length)

  /**
   * Returns a new SubArray with the same beginning as the parent and going
   * until the given i
   */
  def before(i: Int) = slice(0, i)

  /**
   * Returns a new SubArray with the same beginning as the parent and going
   * one further than the given i
   */
  def endingWith(i: Int) = slice(0, i + 1)

  /**
   * Creates a new array with the length of the current slice and eagerly maps 
   * the objects into it.
   */
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

  /**
   * Creates a new array of the length of this slice and copies the values into 
   * it. The exception is when this slice spans the whole length of the internal
   * array. Then the original array is returned.
   */
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

  def sort(ord: Comparator[_ >: T]): Unit = {
    Arrays.sort(
      array.asInstanceOf[Array[Object]],
      from,
      until,
      ord.asInstanceOf[Comparator[Object]])
  }

  def parallelSort(ord: Comparator[_ >: T]): Unit = {
    Arrays.parallelSort(
      array.asInstanceOf[Array[Object]],
      from,
      until,
      ord.asInstanceOf[Comparator[Object]])
  }
}