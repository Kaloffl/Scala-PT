package kaloffl.spath.bvh

import java.util.concurrent.Future
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.SceneObject
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
import kaloffl.spath.scene.LightMaterial
import java.util.Comparator
import kaloffl.spath.scene.Material
import java.util.stream.Collectors
import java.util.function.IntFunction

object Bvh {
  val MAX_LEAF_SIZE = 24
}

class Bvh(objects: Array[SceneObject]) {

  val root: BvhNode = ForkJoinPool.commonPool().invoke(new NodeCreationTask(boxObjects(objects), 0))

  private def boxObjects(objects: Array[SceneObject]): SubArray[BoxedShape] = {
    val boxed = Arrays.stream(objects).parallel.flatMap(
      new Function[SceneObject, Stream[BoxedShape]] {
        override def apply(obj: SceneObject): Stream[BoxedShape] = {

          Arrays.stream(obj.shapes).parallel.map(
            new Function[Shape, BoxedShape] {
              override def apply(shape: Shape): BoxedShape = {
                new BoxedShape(shape, obj.material, shape.enclosingAABB)
              }
            })

        }
      }).toArray(new IntFunction[Array[BoxedShape]] {
        override def apply(length: Int): Array[BoxedShape] = {
          new Array[BoxedShape](length)
        }
      })

    return new SubArray(boxed, 0, boxed.length)
  }

  def getIntersection(ray: Ray): Intersection = {
    class NodeIntersection(val depth: Double, val node: BvhNode)
    // TODO build a array based sorted stack
    // need to figure out the max size. possibly tree-max-depth * 2?
    val stack: SortedStack[NodeIntersection] = new SortedStack(_.depth < _.depth)
    stack add new NodeIntersection(root.depth(ray), root)
    var closest = new Intersection(Double.PositiveInfinity, null, null)
    while (!stack.empty) {
      val head = stack.pop
      if (head.depth >= closest.depth) {
        return closest
      }
      val headNode = head.node
      if (null == headNode.children) {
        val intersection = headNode.intersectObjects(ray)
        if (null != intersection && closest.depth > intersection.depth) {
          closest = intersection
        }
      } else {
        var i = 0
        while (i < headNode.children.length) {
          val child = headNode.children(i)
          val depth = child.depth(ray)
          if (Double.PositiveInfinity != depth && depth < closest.depth) {
            stack add new NodeIntersection(depth, child)
          }
          i += 1
        }
      }
    }
    return closest
  }
}

class NodeCreationTask(objects: SubArray[BoxedShape], level: Int) extends RecursiveTask[BvhNode] {

  //  printf("Created NodeTask, objects: %d, level: %d\n", objects.length, level)

  override def compute: BvhNode = {
    if (objects.length <= Bvh.MAX_LEAF_SIZE) {
      val array = objects.toArray
      val nodeBB = calculateBB[BoxedShape](array, shape ⇒ shape.box)
      return new BvhNode(
        null,
        array.map { _.shape },
        array.map { _.material },
        nodeBB,
        level)
    }

    val padding = 1 //Bvh.MAX_LEAF_SIZE / 2
    val maxChildSize = objects.length - padding
    var smallest = Double.MaxValue
    var bestOrder = 0
    var ordIndex = 0
    var index = -1

    val orderings: Array[Comparator[BoxedShape]] = Array(
      BoxMinXOrder, BoxCenterXOrder, BoxMaxXOrder,
      BoxMinYOrder, BoxCenterYOrder, BoxMaxYOrder,
      BoxMinZOrder, BoxCenterZOrder, BoxMaxZOrder)
    while (ordIndex < orderings.length) {
      objects.sort(orderings(ordIndex))
      val taskA = new SurfaceAreaAccumulator(objects, 0, maxChildSize, 1).fork
      val taskB = new SurfaceAreaAccumulator(objects, objects.length - 1, padding - 1, -1).fork
      val surfaceAreasA = taskA.join
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

    val taskA = new NodeCreationTask(objectsA, level + 1).fork
    val taskB = new NodeCreationTask(objectsB, level + 1).fork

    val children = Array(taskA.join, taskB.join)
    val bb = calculateBB[BvhNode](children, node ⇒ node.aabb)

    if (level % 2 == 0) {
      val collapsed = children.flatMap { node ⇒
        if (null != node.children) node.children
        else Array(node)
      }
      new BvhNode(collapsed, null, null, bb, level)
    } else {
      new BvhNode(children, null, null, bb, level)
    }
  }

  def calculateBB[T](objects: Array[T], unbox: T ⇒ AABB): AABB = {
    var i = 0
    var minX = Double.MaxValue
    var minY = Double.MaxValue
    var minZ = Double.MaxValue
    var maxX = Double.MinValue
    var maxY = Double.MinValue
    var maxZ = Double.MinValue
    while (i < objects.length) {
      val objBB = unbox(objects(i))
      minX = Math.min(minX, objBB.min.x)
      minY = Math.min(minY, objBB.min.y)
      minZ = Math.min(minZ, objBB.min.z)
      maxX = Math.max(maxX, objBB.max.x)
      maxY = Math.max(maxY, objBB.max.y)
      maxZ = Math.max(maxZ, objBB.max.z)
      i += 1
    }
    val min = Vec3d(minX, minY, minZ)
    val max = Vec3d(maxX, maxY, maxZ)
    return new AABB(min, max)
  }

  class SurfaceAreaAccumulator(
      val source: SubArray[BoxedShape],
      val from: Int,
      val until: Int,
      val step: Int) extends RecursiveTask[Array[Double]] {

    //    printf("created task. from %d, until %d, step %d\n", from, until, step)

    override def compute(): Array[Double] = {
      val offset = Math.min(from, until - step)
      val surfaceAreas = new Array[Double](Math.abs(until - from))
      var i = from
      var accumulator = source(i).box
      while (i != until) {
        accumulator = accumulator.enclose(source(i).box)
        surfaceAreas(i - offset) = accumulator.surfaceArea
        i += step
      }
      return surfaceAreas
    }
  }
}

class BoxedShape(val shape: Shape, val material: Material, val box: AABB)

object BoxCenterXOrder extends Comparator[BoxedShape] {
  override def compare(o1: BoxedShape, o2: BoxedShape): Int = {
    val x1 = o1.box.center.x
    val x2 = o2.box.center.x
    if (x1 < x2) return -1
    if (x1 > x2) return 1
    return 0
  }
}
object BoxMinXOrder extends Comparator[BoxedShape] {
  override def compare(o1: BoxedShape, o2: BoxedShape): Int = {
    val x1 = o1.box.min.x
    val x2 = o2.box.min.x
    if (x1 < x2) return -1
    if (x1 > x2) return 1
    return 0
  }
}
object BoxMaxXOrder extends Comparator[BoxedShape] {
  override def compare(o1: BoxedShape, o2: BoxedShape): Int = {
    val x1 = o1.box.max.x
    val x2 = o2.box.max.x
    if (x1 < x2) return -1
    if (x1 > x2) return 1
    return 0
  }
}
object BoxCenterYOrder extends Comparator[BoxedShape] {
  override def compare(o1: BoxedShape, o2: BoxedShape): Int = {
    val y1 = o1.box.center.y
    val y2 = o2.box.center.y
    if (y1 < y2) return -1
    if (y1 > y2) return 1
    return 0
  }
}
object BoxMinYOrder extends Comparator[BoxedShape] {
  override def compare(o1: BoxedShape, o2: BoxedShape): Int = {
    val y1 = o1.box.min.y
    val y2 = o2.box.min.y
    if (y1 < y2) return -1
    if (y1 > y2) return 1
    return 0
  }
}
object BoxMaxYOrder extends Comparator[BoxedShape] {
  override def compare(o1: BoxedShape, o2: BoxedShape): Int = {
    val y1 = o1.box.max.y
    val y2 = o2.box.max.y
    if (y1 < y2) return -1
    if (y1 > y2) return 1
    return 0
  }
}
object BoxCenterZOrder extends Comparator[BoxedShape] {
  override def compare(o1: BoxedShape, o2: BoxedShape): Int = {
    val z1 = o1.box.center.z
    val z2 = o2.box.center.z
    if (z1 < z2) return -1
    if (z1 > z2) return 1
    return 0
  }
}
object BoxMinZOrder extends Comparator[BoxedShape] {
  override def compare(o1: BoxedShape, o2: BoxedShape): Int = {
    val z1 = o1.box.min.z
    val z2 = o2.box.min.z
    if (z1 < z2) return -1
    if (z1 > z2) return 1
    return 0
  }
}
object BoxMaxZOrder extends Comparator[BoxedShape] {
  override def compare(o1: BoxedShape, o2: BoxedShape): Int = {
    val z1 = o1.box.max.z
    val z2 = o2.box.max.z
    if (z1 < z2) return -1
    if (z1 > z2) return 1
    return 0
  }
}