package kaloffl.spath.bvh

import kaloffl.spath.scene.shapes.AABB
import java.util.Comparator
import java.util.concurrent.RecursiveTask
import kaloffl.spath.scene.shapes.Shape
import java.util.concurrent.ForkJoinPool

object BvhBuilder {
  def buildHierarchy(objects: Array[Shape]): BvhNode = {
    println("Building a BVH for " + objects.length + " objects.")
    val start = System.nanoTime

    val root = ForkJoinPool.commonPool().invoke(new NodeCreationTask(new SubArray(objects), 0))

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

class NodeCreationTask(objects: SubArray[Shape], level: Int) extends RecursiveTask[BvhNode] {

  override def compute: BvhNode = {
    if (objects.length <= Bvh.MAX_LEAF_SIZE) {
      val elements = objects.toArray
      val hull = AABB[Shape](elements, _.enclosingAABB)
      return new BvhNode(null, elements, hull, level)
    }

    var lowestCost = Double.MaxValue
    var bestOrdering = 0
    var splittingIndex = -1
    var orderingIndex = 0

    val orderings: Array[Comparator[Shape]] = Array(
      BoxMinXOrder, BoxCenterXOrder, BoxMaxXOrder,
      BoxMinYOrder, BoxCenterYOrder, BoxMaxYOrder,
      BoxMinZOrder, BoxCenterZOrder, BoxMaxZOrder)

    while (orderingIndex < orderings.length) {
      if (0 == level) {
        objects.parallelSort(orderings(orderingIndex))
      } else {
        objects.sort(orderings(orderingIndex))
      }
      val taskA = new LeftSurfaceAreaTask(objects)
      val taskB = new RightSurfaceAreaTask(objects).fork
      val surfaceAreasA = taskA.compute
      val surfaceAreasB = taskB.join

      var i = 0
      val end = objects.length - 1
      while (i < end) {
        val scoreA = surfaceAreasA(i) * i
        val scoreB = surfaceAreasB(i + 1) * (end - i)
        val score = scoreA + scoreB
        if (score < lowestCost) {
          lowestCost = score
          splittingIndex = i
          bestOrdering = orderingIndex
        }
        i += 1
      }
      orderingIndex += 1
    }

    if (bestOrdering != orderings.length - 1) {
      objects.sort(orderings(bestOrdering))
    }

    val objectsA = objects.slice(0, splittingIndex + 1)
    val objectsB = objects.slice(splittingIndex + 1, objects.length)

    val taskA = new NodeCreationTask(objectsA, level + 1)
    val taskB = new NodeCreationTask(objectsB, level + 1).fork

    val children = Array(taskA.compute, taskB.join)
    val bb = AABB[BvhNode](children, _.hull)

    if (level % 2 == 0) {
      val collapsed = children.flatMap { node ⇒
        if (null != node.children) node.children
        else Array(node)
      }
      new BvhNode(collapsed, null, bb, level)
    } else {
      new BvhNode(children, null, bb, level)
    }
  }

  class LeftSurfaceAreaTask(source: SubArray[Shape])
      extends RecursiveTask[Array[Double]] {

    override def compute(): Array[Double] = {
      var i = 0
      var accumulator = source(i).enclosingAABB
      val end = source.length - 1
      val surfaceAreas = new Array[Double](source.length)
      while (i < end) {
        accumulator = accumulator.enclose(source(i).enclosingAABB)
        surfaceAreas(i) = accumulator.surfaceArea
        i += 1
      }
      return surfaceAreas
    }
  }

  class RightSurfaceAreaTask(source: SubArray[Shape])
      extends RecursiveTask[Array[Double]] {

    override def compute(): Array[Double] = {
      var i = source.length - 1
      var accumulator = source(i).enclosingAABB
      val end = 0
      val surfaceAreas = new Array[Double](source.length)
      while (i > end) {
        accumulator = accumulator.enclose(source(i).enclosingAABB)
        surfaceAreas(i) = accumulator.surfaceArea
        i -= 1
      }
      return surfaceAreas
    }
  }
}

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