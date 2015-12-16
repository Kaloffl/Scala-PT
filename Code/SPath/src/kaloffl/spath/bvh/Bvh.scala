package kaloffl.spath.bvh

import java.util.concurrent.Future
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.tracing.Intersection
import kaloffl.spath.tracing.Ray
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.Callable
import java.util.Arrays
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.RecursiveTask
import java.util.function.Function
import java.util.stream.Stream
import java.util.Comparator
import java.util.stream.Collectors
import java.util.function.IntFunction
import kaloffl.spath.scene.materials.Material
import kaloffl.spath.scene.structure.SceneNode

object Bvh {
  val MAX_LEAF_SIZE = 16
}

class Bvh(objects: Array[Shape], material: Material) extends SceneNode {

  println("Building a BVH for " + objects.length + " objects.")
  val start = System.nanoTime

  val root: BvhNode = ForkJoinPool.commonPool().invoke(new NodeCreationTask(new SubArray(objects, 0, objects.length), 0))

  val duration = System.nanoTime - start

  println("Done.")
  if (duration > 1000000000) {
    println("buildtime: " + Math.floor(duration / 10000000.0) / 100.0 + "s")
  } else {
    println("buildtime: " + Math.floor(duration / 10000.0) / 100.0 + "ms")
  }

  override def enclosingAABB: AABB = root.hull

  override def getIntersection(ray: Ray, maxDist: Double): Intersection = {
    class NodeIntersection(val depth: Double, val node: BvhNode)
    // TODO build an array based sorted stack
    val stack: SortedStack[NodeIntersection] = new SortedStack(_.depth < _.depth)
    val rootDepth = root.hullDepth(ray)
    if (rootDepth > maxDist) return null

    var closestIntersection: Intersection = null
    var closestDist= maxDist

    stack add new NodeIntersection(rootDepth, root)
    while (!stack.empty) {
      val head = stack.pop
      if (head.depth >= closestDist) {
        return closestIntersection
      }
      val headNode = head.node
      if (null == headNode.children) {
        val intersection = headNode.intersectElements(ray, maxDist, material)
        if (null != intersection && closestDist > intersection.depth) {
          closestIntersection = intersection
          closestDist = intersection.depth
        }
      } else {
        var i = 0
        while (i < headNode.children.length) {
          val child = headNode.children(i)
          val depth = child.hullDepth(ray)
          if (Double.PositiveInfinity != depth && depth < closestDist) {
            stack add new NodeIntersection(depth, child)
          }
          i += 1
        }
      }
    }
    return closestIntersection
  }
}

class NodeCreationTask(objects: SubArray[Shape], level: Int) extends RecursiveTask[BvhNode] {

  override def compute: BvhNode = {
    if (objects.length <= Bvh.MAX_LEAF_SIZE) {
      val elements = objects.toArray
      val hull = AABB[Shape](elements, _.enclosingAABB)
      return new BvhNode(null, elements, hull, level)
    }

    val padding = 1 //Bvh.MAX_LEAF_SIZE / 2
    val maxChildSize = objects.length - padding
    var smallest = Double.MaxValue
    var bestOrder = 0
    var ordIndex = 0
    var index = -1

    val orderings: Array[Comparator[Shape]] = Array(
      BoxMinXOrder, BoxCenterXOrder, BoxMaxXOrder,
      BoxMinYOrder, BoxCenterYOrder, BoxMaxYOrder,
      BoxMinZOrder, BoxCenterZOrder, BoxMaxZOrder)
    while (ordIndex < orderings.length) {
      objects.sort(orderings(ordIndex))
      val taskA = new SurfaceAreaAccumulator(objects, 0, maxChildSize, 1)
      val taskB = new SurfaceAreaAccumulator(objects, objects.length - 1, padding - 1, -1).fork
      val surfaceAreasA = taskA.compute
      val surfaceAreasB = taskB.join

      var i = padding
      while (i < maxChildSize) {
        val scoreA = surfaceAreasA(i) * (i + 1)
        val i2 = i - padding
        val scoreB = surfaceAreasB(i2) * (maxChildSize - i2)
        val score = scoreA + scoreB
        if (score < smallest) {
          smallest = score
          index = i
          bestOrder = ordIndex
        }
        i += 1
      }
      ordIndex += 1
    }

    if (bestOrder != orderings.length - 1) {
      objects.sort(orderings(bestOrder))
    }

    val objectsA = objects.slice(0, index + 1)
    val objectsB = objects.slice(index + 1, objects.length)

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

  class SurfaceAreaAccumulator(
      val source: SubArray[Shape],
      val from: Int,
      val until: Int,
      val step: Int) extends RecursiveTask[Array[Double]] {

    //    printf("created task. from %d, until %d, step %d\n", from, until, step)

    override def compute(): Array[Double] = {
      val offset = Math.min(from, until - step)
      val surfaceAreas = new Array[Double](Math.abs(until - from))
      var i = from
      var accumulator = source(i).enclosingAABB
      while (i != until) {
        accumulator = accumulator.enclose(source(i).enclosingAABB)
        surfaceAreas(i - offset) = accumulator.surfaceArea
        i += step
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