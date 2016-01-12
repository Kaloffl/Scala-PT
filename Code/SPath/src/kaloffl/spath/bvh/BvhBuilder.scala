package kaloffl.spath.bvh

import java.util.Comparator
import kaloffl.jobs.Job
import kaloffl.jobs.JobPool
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.shapes.Enclosable
import kaloffl.spath.scene.shapes.Intersectable
import kaloffl.spath.scene.structure.SceneNode

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
  def buildBvh(objects: Array[Shape], material: Material): ShapeBvh = {
    println("Building a BVH for " + objects.length + " objects.")
    val start = System.nanoTime

    val pool = new JobPool

    var root: BvhNode[Shape] = null
    pool.submit(new SplittingJob[Shape](pool, new SubArray(objects), root = _, 0))
    pool.execute

    val duration = System.nanoTime - start

    println("Done.")
    if (duration > 1000000000) {
      println("buildtime: " + Math.floor(duration / 10000000.0) / 100.0 + "s")
    } else {
      println("buildtime: " + Math.floor(duration / 10000.0) / 100.0 + "ms")
    }
    return new ShapeBvh(root, material);
  }
  
  def buildBvh(objects: Array[SceneNode]): ObjectBvh = {
    println("Building a BVH for " + objects.length + " objects.")
    val start = System.nanoTime

    val pool = new JobPool

    var root: BvhNode[SceneNode] = null
    pool.submit(new SplittingJob[SceneNode](pool, new SubArray(objects), root = _, 0))
    pool.execute

    val duration = System.nanoTime - start

    println("Done.")
    if (duration > 1000000000) {
      println("buildtime: " + Math.floor(duration / 10000000.0) / 100.0 + "s")
    } else {
      println("buildtime: " + Math.floor(duration / 10000.0) / 100.0 + "ms")
    }
    return new ObjectBvh(root);
  }
  
}

class SplittingJob[T <: Enclosable with Intersectable](
    jobPool: JobPool,
    objects: SubArray[T],
    consumer: BvhNode[T] ⇒ Unit,
    level: Int) extends Job {

  override def canExecute = true

  override def execute: Unit = {
    // If the array is small enough we create a leaf and return
    if (objects.length <= Bvh.MAX_LEAF_SIZE) {
      val elements = objects.toArray
      val hull = AABB.enclosing[T](elements, _.enclosingAABB)
      consumer(new BvhNode[T](null, elements, hull, level))
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
        backToFront(i + 1) = objects(i + 1).enclosingAABB
        val end = 0
        while (i > end) {
          backToFront(i) = backToFront(i + 1).enclose(objects(i).enclosingAABB)
          i -= 1
        }
      }
      // then iterate from the front to the back, calculate the surface areas 
      // on the fly and compare the score to find the best splitting point
      {
        var i = 0
        val end = objects.length - 1
        var accumulator = objects(i).enclosingAABB
        while (i < end) {
          accumulator = accumulator.enclose(objects(i).enclosingAABB)
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

    val objectsA = objects.slice(0, splittingIndex + 1)
    val objectsB = objects.slice(splittingIndex + 1, objects.length)

    val merge = new MergeJob(level, consumer)

    jobPool.submit(new SplittingJob[T](jobPool, objectsA, n ⇒ merge.left = n, level + 1))
    jobPool.submit(new SplittingJob[T](jobPool, objectsB, n ⇒ merge.right = n, level + 1))

    jobPool.submit(merge)
  }
}

class MergeJob[T <: Intersectable](level: Int, consumer: BvhNode[T] ⇒ Unit) extends Job {

  var left: BvhNode[T] = null
  var right: BvhNode[T] = null

  override def canExecute = (null != left && null != right)

  override def execute: Unit = {
    val children = Array(left, right)
    val bb = AABB.enclosing[BvhNode[T]](children, _.hull) 

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

object BoxCenterXOrder extends Comparator[Enclosable] {
  override def compare(e1: Enclosable, e2: Enclosable): Int = {
    val x1 = e1.enclosingAABB.center.x
    val x2 = e2.enclosingAABB.center.x
    if (x1 < x2) return -1
    if (x1 > x2) return 1
    return 0
  }
}
object BoxMinXOrder extends Comparator[Enclosable] {
  override def compare(e1: Enclosable, e2: Enclosable): Int = {
    val x1 = e1.enclosingAABB.min.x
    val x2 = e2.enclosingAABB.min.x
    if (x1 < x2) return -1
    if (x1 > x2) return 1
    return 0
  }
}
object BoxMaxXOrder extends Comparator[Enclosable] {
  override def compare(e1: Enclosable, e2: Enclosable): Int = {
    val x1 = e1.enclosingAABB.max.x
    val x2 = e2.enclosingAABB.max.x
    if (x1 < x2) return -1
    if (x1 > x2) return 1
    return 0
  }
}
object BoxCenterYOrder extends Comparator[Enclosable] {
  override def compare(e1: Enclosable, e2: Enclosable): Int = {
    val y1 = e1.enclosingAABB.center.y
    val y2 = e2.enclosingAABB.center.y
    if (y1 < y2) return -1
    if (y1 > y2) return 1
    return 0
  }
}
object BoxMinYOrder extends Comparator[Enclosable] {
  override def compare(e1: Enclosable, e2: Enclosable): Int = {
    val y1 = e1.enclosingAABB.min.y
    val y2 = e2.enclosingAABB.min.y
    if (y1 < y2) return -1
    if (y1 > y2) return 1
    return 0
  }
}
object BoxMaxYOrder extends Comparator[Enclosable] {
  override def compare(e1: Enclosable, e2: Enclosable): Int = {
    val y1 = e1.enclosingAABB.max.y
    val y2 = e2.enclosingAABB.max.y
    if (y1 < y2) return -1
    if (y1 > y2) return 1
    return 0
  }
}
object BoxCenterZOrder extends Comparator[Enclosable] {
  override def compare(e1: Enclosable, e2: Enclosable): Int = {
    val z1 = e1.enclosingAABB.center.z
    val z2 = e2.enclosingAABB.center.z
    if (z1 < z2) return -1
    if (z1 > z2) return 1
    return 0
  }
}
object BoxMinZOrder extends Comparator[Enclosable] {
  override def compare(e1: Enclosable, e2: Enclosable): Int = {
    val z1 = e1.enclosingAABB.min.z
    val z2 = e2.enclosingAABB.min.z
    if (z1 < z2) return -1
    if (z1 > z2) return 1
    return 0
  }
}
object BoxMaxZOrder extends Comparator[Enclosable] {
  override def compare(e1: Enclosable, e2: Enclosable): Int = {
    val z1 = e1.enclosingAABB.max.z
    val z2 = e2.enclosingAABB.max.z
    if (z1 < z2) return -1
    if (z1 > z2) return 1
    return 0
  }
}