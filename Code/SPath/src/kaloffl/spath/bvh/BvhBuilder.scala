package kaloffl.spath.bvh

import java.util.Comparator
import kaloffl.jobs.Job
import kaloffl.jobs.JobPool
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Bounded
import kaloffl.spath.scene.shapes.Intersectable
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.scene.structure.SceneNode

object BvhBuilder {

  val MaxLeafSize = 8

  /**
   * Takes an array of objects and creates a Bounding Volume Hierarchy out of
   * Axis Aligned Bounding Boxes for quick intersection checking with all the
   * shapes.<br>
   * The algorithm iterates top-down on the array of shapes. It will continue
   * to split the array in halves until each piece is under a maximum size. The
   * splitting is done in places where the two smallest AABBs will be created
   * around the children.
   */
  def buildTree[T <: Bounded with Intersectable](objects: Array[T]): Bvh[T] = {
    println("Building a BVH for " + objects.length + " objects.")
    val start = System.nanoTime

    val pool = new JobPool

    var root: Bvh[T] = null
    pool.submit(
      new SplittingJob[T](
        jobPool = pool,
        objects = new SubArray(objects),
        consumer = { root = _ },
        level = 0))
    pool.execute

    val duration = System.nanoTime - start

    println("Done.")
    if (duration > 1000000000) {
      println("buildtime: " + Math.floor(duration / 10000000.0) / 100.0 + "s")
    } else {
      println("buildtime: " + Math.floor(duration / 10000.0) / 100.0 + "ms")
    }
    return root
  }
}

class SplittingJob[T <: Bounded with Intersectable](
    jobPool: JobPool,
    objects: SubArray[T],
    consumer: Bvh[T] ⇒ Unit,
    level: Int) extends Job {

  override def execute: Unit = {
    // If the array is small enough we create a leaf and return
    if (objects.length <= BvhBuilder.MaxLeafSize) {
      val elements = objects.toArray
      val hull = AABB.enclosing[T](elements, _.getBounds)
      consumer(new Bvh[T](null, elements, hull, level))
      return
    }
    // otherwise we look for the best place to split the array

    var lowestCost = Double.MaxValue
    var bestOrdering = 0
    var splittingIndex = -1
    var orderingIndex = 0

    val orderings = Array(
      BoxMinXOrder, BoxCenterXOrder, BoxMaxXOrder,
      BoxMinYOrder, BoxCenterYOrder, BoxMaxYOrder,
      BoxMinZOrder, BoxCenterZOrder, BoxMaxZOrder)

    while (orderingIndex < orderings.length) {
      // small optimization: on the first level there is only one job being 
      // done, so we can sort in parallel and get the full benefit
      if (0 == level) {
        // TODO use a custom JobPool based sort
        objects.parallelSort(orderings(orderingIndex))
      } else {
        objects.sort(orderings(orderingIndex))
      }

      // first calculate the enclosing AABBs from the back to the front
      val backToFront = new Array[AABB](objects.length);
      {
        var i = objects.length - 2
        backToFront(i + 1) = objects(i + 1).getBounds
        val end = 0
        while (i > end) {
          backToFront(i) = backToFront(i + 1).enclose(objects(i).getBounds)
          i -= 1
        }
      }
      // then iterate from the front to the back, calculate the surface areas 
      // on the fly and compare the score to find the best splitting point
      {
        var i = 0
        val end = objects.length - 1
        var accumulator = objects(i).getBounds
        while (i < end) {
          accumulator = accumulator.enclose(objects(i).getBounds)
          val scoreA = accumulator.surfaceArea * i
          val scoreB = backToFront(i + 1).surfaceArea * (end - i)
          val scoreC = accumulator.overlap(backToFront(i + 1)).surfaceArea
          val score = scoreA + scoreB + scoreC
          if (score < lowestCost) {
            lowestCost = score
            splittingIndex = i
            bestOrdering = orderingIndex
          }
          i += 1
        }
      }
      orderingIndex += 1
    }

    if (bestOrdering != orderings.length - 1) {
      if (0 == level) {
        // TODO use a custom JobPool based sort
        objects.parallelSort(orderings(bestOrdering))
      } else {
        objects.sort(orderings(bestOrdering))
      }
    }

    val objectsA = objects.endingWith(splittingIndex)
    val objectsB = objects.after(splittingIndex)

    val merge = new MergeJob(level, consumer)

    jobPool.submit(new SplittingJob[T](jobPool, objectsA, n ⇒ merge.left = n, level + 1))
    jobPool.submit(new SplittingJob[T](jobPool, objectsB, n ⇒ merge.right = n, level + 1))

    jobPool.submit(merge)
  }
}

class MergeJob[T <: Intersectable](level: Int, consumer: Bvh[T] ⇒ Unit) extends Job {

  var left: Bvh[T] = null
  var right: Bvh[T] = null

  override def canExecute = (null != left && null != right)

  override def execute: Unit = {
    val children = Array(left, right)
    val bb = AABB.enclosing[Bvh[T]](children, _.hull)

    // Every two levels we collapse the previous level into the current one to
    // create a 4-way tree instead of a binary one.
    if (level % 2 == 0) {
      val collapsed = children.flatMap { node ⇒
        if (null != node.children) node.children
        else Array(node)
      }
      consumer(new Bvh(collapsed, null, bb, level))
    } else {
      consumer(new Bvh(children, null, bb, level))
    }
  }
}

// Following are the implementations of the nine different ways to sort. nothing
// too exiting.

object BoxCenterXOrder extends Comparator[Bounded] {
  override def compare(e1: Bounded, e2: Bounded): Int = {
    val x1 = e1.getBounds.center.x
    val x2 = e2.getBounds.center.x
    if (x1 < x2) return -1
    if (x1 > x2) return 1
    return 0
  }
}
object BoxMinXOrder extends Comparator[Bounded] {
  override def compare(e1: Bounded, e2: Bounded): Int = {
    val x1 = e1.getBounds.min.x
    val x2 = e2.getBounds.min.x
    if (x1 < x2) return -1
    if (x1 > x2) return 1
    return 0
  }
}
object BoxMaxXOrder extends Comparator[Bounded] {
  override def compare(e1: Bounded, e2: Bounded): Int = {
    val x1 = e1.getBounds.max.x
    val x2 = e2.getBounds.max.x
    if (x1 < x2) return -1
    if (x1 > x2) return 1
    return 0
  }
}
object BoxCenterYOrder extends Comparator[Bounded] {
  override def compare(e1: Bounded, e2: Bounded): Int = {
    val y1 = e1.getBounds.center.y
    val y2 = e2.getBounds.center.y
    if (y1 < y2) return -1
    if (y1 > y2) return 1
    return 0
  }
}
object BoxMinYOrder extends Comparator[Bounded] {
  override def compare(e1: Bounded, e2: Bounded): Int = {
    val y1 = e1.getBounds.min.y
    val y2 = e2.getBounds.min.y
    if (y1 < y2) return -1
    if (y1 > y2) return 1
    return 0
  }
}
object BoxMaxYOrder extends Comparator[Bounded] {
  override def compare(e1: Bounded, e2: Bounded): Int = {
    val y1 = e1.getBounds.max.y
    val y2 = e2.getBounds.max.y
    if (y1 < y2) return -1
    if (y1 > y2) return 1
    return 0
  }
}
object BoxCenterZOrder extends Comparator[Bounded] {
  override def compare(e1: Bounded, e2: Bounded): Int = {
    val z1 = e1.getBounds.center.z
    val z2 = e2.getBounds.center.z
    if (z1 < z2) return -1
    if (z1 > z2) return 1
    return 0
  }
}
object BoxMinZOrder extends Comparator[Bounded] {
  override def compare(e1: Bounded, e2: Bounded): Int = {
    val z1 = e1.getBounds.min.z
    val z2 = e2.getBounds.min.z
    if (z1 < z2) return -1
    if (z1 > z2) return 1
    return 0
  }
}
object BoxMaxZOrder extends Comparator[Bounded] {
  override def compare(e1: Bounded, e2: Bounded): Int = {
    val z1 = e1.getBounds.max.z
    val z2 = e2.getBounds.max.z
    if (z1 < z2) return -1
    if (z1 > z2) return 1
    return 0
  }
}