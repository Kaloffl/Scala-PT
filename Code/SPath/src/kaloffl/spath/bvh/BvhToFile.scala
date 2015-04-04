package kaloffl.spath.bvh

import java.io.FileOutputStream
import java.io.PrintStream
import kaloffl.spath.scene.shapes.AABB
import kaloffl.spath.math.Vec3d

object BvhToFile {

  def toFile(bvh: Bvh, file: String): Unit = {
    val out = new PrintStream(new FileOutputStream(file))

    printNode(bvh.root, out, 0)

    out.close()
  }

  def printNode(node: BvhNode, out: PrintStream, level: Int): Unit = {

    for (i ← 0 until level) out.print("  ")
    printAABB(node.aabb, out)
    if (null != node.shapes) {
      out.print(" c: ")
      out.print(node.shapes.length)
    }
    out.print('\n')

    if (null != node.children) {
      var i = 0
      while (i < node.children.length) {
        printNode(node.children(i), out, level + 1)
        i += 1
      }
    }
  }

  def printAABB(aabb: AABB, out: PrintStream): Unit = {
    out.print("AABB(min:")
    printVec3d(aabb.min, out)
    out.print(", size:")
    printVec3d(aabb.size, out)
    out.print(")")
  }

  def printVec3d(v: Vec3d, out: PrintStream): Unit = {
    out.print("Vec3d(x:")
    out.print(f"${v.x}%.4f")
    out.print(", y:")
    out.print(f"${v.y}%.4f")
    out.print(", z:")
    out.print(f"${v.z}%.4f")
    out.print(")")
  }
}