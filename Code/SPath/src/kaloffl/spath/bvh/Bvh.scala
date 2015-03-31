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
import kaloffl.spath.scene.LightMaterial
import java.util.Comparator

class Bvh(objects: Array[SceneObject]) {

  val root: BvhNode = ForkJoinPool.commonPool().invoke(new NodeCreationTask(boxObjects(objects), 0))

  private def boxObjects(objects: Array[SceneObject]): SubArray[BoxedObject] = {
    val boxed = new Array[BoxedObject](objects.length)
    var i = 0
    while (i < boxed.length) {
      boxed(i) = new BoxedObject(objects(i), objects(i).shape.enclosingAABB)
      i += 1
    }
    return new SubArray(boxed, 0, boxed.length)
  }

  def getIntersection(ray: Ray): Intersection = {
    class NodeIntersection(val depth: Double, val node: BvhNode)
    // TODO build a array based sorted stack
    // need to figure out the max size. possibly tree-max-depth * 2?
    val stack: SortedStack[NodeIntersection] = new SortedStack(_.depth < _.depth)
    stack add new NodeIntersection(root.depth(ray), root)
    var closest = new Intersection(Double.PositiveInfinity, null)
    while (!stack.empty) {
      val head = stack.pop
      if (head.depth >= closest.depth) {
        return closest
      }
      val headNode = head.node
      if (null != headNode.objects) {
        val intersection = headNode.intersectObjects(ray)
        if (null != intersection && closest.depth > intersection.depth) {
          closest = intersection
        }
      } else {
        val childA = headNode.childA
        val depthA = childA.depth(ray)
        if (!depthA.isInfinite && depthA < closest.depth) {
          stack add new NodeIntersection(depthA, childA)
        }
        val childB = headNode.childB
        val depthB = childB.depth(ray)
        if (!depthB.isInfinite && depthB < closest.depth) {
          stack add new NodeIntersection(depthB, childB)
        }
      }
    }
    return closest
  }

  override def toString: String = {
    def nodeToString(builder: StringBuilder, level: Int, node: BvhNode): String = {
      if (0 < level) {
        for (i ← 0 until level - 1) {
          builder.append('|')
        }
        builder.append("+->")
      }
      builder.append("Node(")
      builder.append("AABB(").append("min = ").append(node.aabb.min)
      builder.append(", size = ").append(node.aabb.size).append(")")
      builder.append(", objects: ")
      if (null == node.objects) {
        builder.append(0)
      } else {
        builder.append(node.objects.length)
      }
      builder.append(")\n")
      if (null != node.childA) {
        nodeToString(builder, level + 1, node.childA)
      }
      if (null != node.childB) {
        nodeToString(builder, level + 1, node.childB)
      }
      builder.toString
    }
    val builder = new StringBuilder
    return nodeToString(builder, 0, root)
  }
}

class BvhNode(
    val childA: BvhNode,
    val childB: BvhNode,
    val objects: Array[SceneObject],
    val aabb: AABB,
    val level: Int) {

  def depth(ray: Ray): Double = if (aabb.contains(ray.start)) 0.0 else aabb.getIntersectionDepth(ray)

  def intersectObjects(ray: Ray): Intersection = {
    var minDepth: Double = Double.PositiveInfinity
    var hitIndex: Int = -1
    var i = 0
    while (i < objects.length) {
      val depth = objects(i).shape.getIntersectionDepth(ray)
      if (depth < minDepth) {
        minDepth = depth
        hitIndex = i
      }
      i += 1
    }
    if (0 > hitIndex) return null
    return new Intersection(minDepth, objects(hitIndex))
  }
}

class NodeCreationTask(objects: SubArray[BoxedObject], level: Int) extends RecursiveTask[BvhNode] {

  //  printf("Created NodeTask, objects: %d, level: %d\n", objects.length, level)

  override def compute: BvhNode = {
    if (objects.length <= 24) {
      val nodeBB = calculateBB(objects)
      return new BvhNode(null, null, objects.map { _.obj }.toArray, nodeBB, level)
    }

    val maxChildSize = objects.length - 1
    var smallest = Double.MaxValue
    var bestOrder = 0
    var ordIndex = 0
    var index = -1

    val orderings: Array[Comparator[BoxedObject]] = Array(
      // TODO move implementations out into objects
      new Comparator[BoxedObject]() {
        override def compare(o1: BoxedObject, o2: BoxedObject): Int = {
          val x1 = o1.box.center.x
          val x2 = o2.box.center.x
          if (x1 < x2) return -1
          if (x1 > x2) return 1
          return 0
        }
      },
      new Comparator[BoxedObject]() {
        override def compare(o1: BoxedObject, o2: BoxedObject): Int = {
          val x1 = o1.box.min.x
          val x2 = o2.box.min.x
          if (x1 < x2) return -1
          if (x1 > x2) return 1
          return 0
        }
      },
      new Comparator[BoxedObject]() {
        override def compare(o1: BoxedObject, o2: BoxedObject): Int = {
          val x1 = o1.box.max.x
          val x2 = o2.box.max.x
          if (x1 < x2) return -1
          if (x1 > x2) return 1
          return 0
        }
      },
      new Comparator[BoxedObject]() {
        override def compare(o1: BoxedObject, o2: BoxedObject): Int = {
          val y1 = o1.box.center.y
          val y2 = o2.box.center.y
          if (y1 < y2) return -1
          if (y1 > y2) return 1
          return 0
        }
      },
      new Comparator[BoxedObject]() {
        override def compare(o1: BoxedObject, o2: BoxedObject): Int = {
          val y1 = o1.box.min.y
          val y2 = o2.box.min.y
          if (y1 < y2) return -1
          if (y1 > y2) return 1
          return 0
        }
      },
      new Comparator[BoxedObject]() {
        override def compare(o1: BoxedObject, o2: BoxedObject): Int = {
          val y1 = o1.box.max.y
          val y2 = o2.box.max.y
          if (y1 < y2) return -1
          if (y1 > y2) return 1
          return 0
        }
      },
      new Comparator[BoxedObject]() {
        override def compare(o1: BoxedObject, o2: BoxedObject): Int = {
          val z1 = o1.box.center.z
          val z2 = o2.box.center.z
          if (z1 < z2) return -1
          if (z1 > z2) return 1
          return 0
        }
      },
      new Comparator[BoxedObject]() {
        override def compare(o1: BoxedObject, o2: BoxedObject): Int = {
          val z1 = o1.box.min.z
          val z2 = o2.box.min.z
          if (z1 < z2) return -1
          if (z1 > z2) return 1
          return 0
        }
      },
      new Comparator[BoxedObject]() {
        override def compare(o1: BoxedObject, o2: BoxedObject): Int = {
          val z1 = o1.box.max.z
          val z2 = o2.box.max.z
          if (z1 < z2) return -1
          if (z1 > z2) return 1
          return 0
        }
      })
    while (ordIndex < orderings.length) {
      objects.sort(orderings(ordIndex))
      val taskA = new SurfaceAreaAccumulator(objects, 0, maxChildSize, 1).fork
      val taskB = new SurfaceAreaAccumulator(objects, maxChildSize, 0, -1).fork
      val surfaceAreasA = taskA.join
      val surfaceAreasB = taskB.join

      var i = 0
      while (i < maxChildSize) {
        val scoreA = surfaceAreasA(i) * (i + 1)
        val scoreB = surfaceAreasB(i) * (maxChildSize - i)
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

    if (bestOrder != orderings.length + 1) {
      objects.sort(orderings(bestOrder))
    }

    val objectsA = objects.slice(0, index + 1)
    val objectsB = objects.slice(index + 1, maxChildSize + 1)

    val taskA = new NodeCreationTask(objectsA, level + 1).fork
    val taskB = new NodeCreationTask(objectsB, level + 1).fork

    val childA = taskA.join
    val childB = taskB.join

    new BvhNode(childA, childB, null, childA.aabb.enclose(childB.aabb), level)
  }

  def calculateBB(objects: SubArray[BoxedObject]): AABB = {
    var i = 0
    var minX = Double.MaxValue
    var minY = Double.MaxValue
    var minZ = Double.MaxValue
    var maxX = Double.MinValue
    var maxY = Double.MinValue
    var maxZ = Double.MinValue
    while (i < objects.length) {
      val objBB = objects(i).box
      minX = Math.min(minX, objBB.min.x)
      minY = Math.min(minY, objBB.min.y)
      minZ = Math.min(minZ, objBB.min.z)
      maxX = Math.max(maxX, objBB.max.x)
      maxY = Math.max(maxY, objBB.max.y)
      maxZ = Math.max(maxZ, objBB.max.z)
      i += 1
    }
    val min = Vec3d(minX, minY, minZ)
    val size = Vec3d(maxX, maxY, maxZ) - min
    return new AABB(min + size / 2, size)
  }

  class SurfaceAreaAccumulator(
      val source: SubArray[BoxedObject],
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

class BoxedObject(val obj: SceneObject, val box: AABB)