package kaloffl.spath.bvh

import java.util.Comparator

import kaloffl.jobs.Job
import kaloffl.jobs.JobPool
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Shape

object BvhBuilder {

  /**
   * Takes a list of shapes and creates a Bounding Volume Hierarchy out of
   * Axis Aligned Bounding Boxes for quick intersection checking with all the
   * shapes.<br>
   * The algorithm iterates top-down on the array of shapes. It will continue
   * to split the array in halves until each piece is under a maximum size. The
   * splitting is done in places where the two smallest AABBs will be created
   * around the children.
   */
  def buildHierarchy(objects: Array[Shape]): BvhNode = {
    println("Building a BVH for " + objects.length + " objects.")
    val start = System.nanoTime

    val pool = new JobPool

    var root: BvhNode = null
    pool.submit(new SplittingJob(pool, new SubArray(objects), root = _, 0))
    pool.execute

    val duration = System.nanoTime - start

    println("Done.")
    if (duration > 1000000000) {
      println("buildtime: " + Math.floor(duration / 10000000.0) / 100.0 + "s")
    } else {
      println("buildtime: " + Math.floor(duration / 10000.0) / 100.0 + "ms")
    }
    return root;
  }
}

class SplittingJob(
    jobPool: JobPool,
    objects: SubArray[Shape],
    consumer: BvhNode ⇒ Unit,
    level: Int) extends Job {

  override def canExecute = true

  override def execute: Unit = {
    // If the array is small enough we create a leaf and return
    if (objects.length <= Bvh.MAX_LEAF_SIZE) {
      val elements = objects.toArray
      val hull = AABB[Shape](elements, _.enclosingAABB)
      consumer(new BvhNode(null, elements, hull, level))
      return
    }
    // otherwise we look for the best place to split the array

    var lowestCost = Double.MaxValue
    var bestOrdering = 0
    var splittingIndex = -1
    var orderingIndex = 0

    val orderings: Array[Comparator[Shape]] = Array(
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

      // first calculate the surface areas from the back to the front
      val rightSurfaceAreas = new Array[Double](objects.length);
      {
        var i = objects.length - 1
        var accumulator = objects(i).enclosingAABB
        val end = 0
        while (i > end) {
          accumulator = accumulator.enclose(objects(i).enclosingAABB)
          rightSurfaceAreas(i) = accumulator.surfaceArea
          i -= 1
        }
      }
      // then iterate from the front to the back, calculate the surface area 
      // on the fly and compare the score to find the best splitting point
      {
        var i = 0
        val end = objects.length - 1
        var accumulator = objects(i).enclosingAABB
        while (i < end) {
          accumulator = accumulator.enclose(objects(i).enclosingAABB)
          val scoreA = accumulator.surfaceArea * i
          val scoreB = rightSurfaceAreas(i + 1) * (end - i)
          val score = scoreA + scoreB
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

    val objectsA = objects.slice(0, splittingIndex + 1)
    val objectsB = objects.slice(splittingIndex + 1, objects.length)

    val merge = new MergeJob(level, consumer)

    jobPool.submit(new SplittingJob(jobPool, objectsA, n ⇒ merge.left = n, level + 1))
    jobPool.submit(new SplittingJob(jobPool, objectsB, n ⇒ merge.right = n, level + 1))

    jobPool.submit(merge)
  }
}

class MergeJob(level: Int, consumer: BvhNode ⇒ Unit) extends Job {

  var left: BvhNode = null
  var right: BvhNode = null

  override def canExecute = (null != left && null != right)

  override def execute: Unit = {
    val children = Array(left, right)
    val bb = AABB[BvhNode](children, _.hull)

    // Every two levels we collapse the previous level into the current one to
    // create a 4-way tree instead of a binary one.
    if (level % 2 == 0) {
      val collapsed = children.flatMap { node ⇒
        if (null != node.children) node.children
        else Array(node)
      }
      consumer(new BvhNode(collapsed, null, bb, level))
    } else {
      consumer(new BvhNode(children, null, bb, level))
    }
  }
}

// Following are the implementations of the nine different ways to sort. nothing
// too exiting.

object BoxCenterXOrder extends Comparator[Shape] {
  override def compare(shape1: Shape, shape2: Shape): Int = {
    val x1 = shape1.enclosingAABB.center.x
    val x2 = shape2.enclosingAABB.center.x
    if (x1 < x2) return -1
    if (x1 > x2) return 1
    return 0
  }
}
object BoxMinXOrder extends Comparator[Shape] {
  override def compare(shape1: Shape, shape2: Shape): Int = {
    val x1 = shape1.enclosingAABB.min.x
    val x2 = shape2.enclosingAABB.min.x
    if (x1 < x2) return -1
    if (x1 > x2) return 1
    return 0
  }
}
object BoxMaxXOrder extends Comparator[Shape] {
  override def compare(shape1: Shape, shape2: Shape): Int = {
    val x1 = shape1.enclosingAABB.max.x
    val x2 = shape2.enclosingAABB.max.x
    if (x1 < x2) return -1
    if (x1 > x2) return 1
    return 0
  }
}
object BoxCenterYOrder extends Comparator[Shape] {
  override def compare(shape1: Shape, shape2: Shape): Int = {
    val y1 = shape1.enclosingAABB.center.y
    val y2 = shape2.enclosingAABB.center.y
    if (y1 < y2) return -1
    if (y1 > y2) return 1
    return 0
  }
}
object BoxMinYOrder extends Comparator[Shape] {
  override def compare(shape1: Shape, shape2: Shape): Int = {
    val y1 = shape1.enclosingAABB.min.y
    val y2 = shape2.enclosingAABB.min.y
    if (y1 < y2) return -1
    if (y1 > y2) return 1
    return 0
  }
}
object BoxMaxYOrder extends Comparator[Shape] {
  override def compare(shape1: Shape, shape2: Shape): Int = {
    val y1 = shape1.enclosingAABB.max.y
    val y2 = shape2.enclosingAABB.max.y
    if (y1 < y2) return -1
    if (y1 > y2) return 1
    return 0
  }
}
object BoxCenterZOrder extends Comparator[Shape] {
  override def compare(shape1: Shape, shape2: Shape): Int = {
    val z1 = shape1.enclosingAABB.center.z
    val z2 = shape2.enclosingAABB.center.z
    if (z1 < z2) return -1
    if (z1 > z2) return 1
    return 0
  }
}
object BoxMinZOrder extends Comparator[Shape] {
  override def compare(shape1: Shape, shape2: Shape): Int = {
    val z1 = shape1.enclosingAABB.min.z
    val z2 = shape2.enclosingAABB.min.z
    if (z1 < z2) return -1
    if (z1 > z2) return 1
    return 0
  }
}
object BoxMaxZOrder extends Comparator[Shape] {
  override def compare(shape1: Shape, shape2: Shape): Int = {
    val z1 = shape1.enclosingAABB.max.z
    val z2 = shape2.enclosingAABB.max.z
    if (z1 < z2) return -1
    if (z1 > z2) return 1
    return 0
  }
}