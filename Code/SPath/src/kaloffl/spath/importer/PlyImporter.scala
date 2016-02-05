package kaloffl.spath.importer

import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.util.Scanner
import java.util.regex.PatternSyntaxException

import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.shapes.Shape
import kaloffl.spath.scene.shapes.Triangle

/**
 * @author Lars
 */
object PlyImporter {

  def load(file: String, scale: Vec3d = Vec3d.Unit, offset: Vec3d = Vec3d.Origin): Array[Triangle] = {

    val inputStream = new BufferedInputStream(new FileInputStream(file))
    inputStream.mark(1024) // TODO rewrite importer so no buffer for resetting is needed
    val scanner = new Scanner(inputStream)

    var binary: Boolean = false
    var vertCount = 0
    var faceCount = 0

    println("Parsing header")
    var header = true
    var headerSize = 0
    while (header && scanner.hasNextLine()) {
      val line = scanner.nextLine
      val segments = line.split(' ')

      headerSize += line.length + 1
      println(line)

      if (segments(0) equals "format") {
        binary = !("ascii" equals segments(1))
      }

      if (segments(0) equals "element") {
        if (segments(1) equals "vertex") {
          vertCount = Integer.parseInt(segments(2))
          println("Vertecies: " + vertCount)
        } else if (segments(1) equals "face") {
          faceCount = Integer.parseInt(segments(2))
          println("faces: " + faceCount)
        }
      } else if (segments(0) equals ("end_header")) {
        header = false
      }
    }

    val vertecies: Array[Vec3d] = new Array(vertCount)
    val faces: Array[Triangle] = new Array(faceCount)

    println("Reading vertecies")
    if (binary) {
      inputStream.reset()
      inputStream.skip(headerSize)

      val data = new Array[Byte](4)
      def readFloat(input: InputStream): Float = {
        if (4 != input.read(data)) {
          throw new RuntimeException("Bytes in stream didn't line for float conversion.")
        }
        val i = (data(3) & 0xff) | ((data(2) & 0xff) << 8) | ((data(1) & 0xff) << 16) | ((data(0) & 0xff) << 24)
        return java.lang.Float.intBitsToFloat(i)
      }

      var i = 0
      while (i < vertecies.length) {
        val x = readFloat(inputStream) // * scale.x + offset.x
        val y = readFloat(inputStream) // * scale.y + offset.y
        val z = readFloat(inputStream) // * scale.z + offset.z
        vertecies(i) = Vec3d(x, y, z)
        if (i % (vertecies.length / 10) == 0 && i > 0) {
          printf("%2d%%... ", i * 100 / vertecies.length)
        }
        i += 1
      }
      println
    } else {
      var i = 0
      while (i < vertecies.length) {
        val line = scanner.nextLine
        val segments = line.split(' ')
        val x = java.lang.Double.parseDouble(segments(0)) * scale.x + offset.x
        val y = java.lang.Double.parseDouble(segments(1)) * scale.y + offset.y
        val z = java.lang.Double.parseDouble(segments(2)) * scale.z + offset.z
        vertecies(i) = Vec3d(x, y, z)
        if (i % (vertecies.length / 10) == 0 && i > 0) {
          printf("%2d%%... ", i * 100 / vertecies.length)
        }
        i += 1
      }
      println
    }

    println("Reading face indices")

    if (binary) {
      val data = new Array[Byte](4)
      def readInt(input: InputStream): Int = {
        if (4 != input.read(data)) {
          throw new RuntimeException("Bytes in stream didn't line for float conversion.")
        }
        val i = (data(3) & 0xff) | ((data(2) & 0xff) << 8) | ((data(1) & 0xff) << 16) | ((data(0) & 0xff) << 24)
        if (i < 0) {
          throw new RuntimeException(s"Negative index $i found. File must be corrupt.")
        }
        if (i >= vertecies.length) {
          throw new RuntimeException(s"Index $i too large. File must be corrupt.")
        }
        return i
      }

      var i = 0
      while (i < faces.length) {
        val length = inputStream.read
        if (3 != length) {
          throw new RuntimeException("Can't handle faces with more than 3 vertecies.")
        }
        val vertA = vertecies(readInt(inputStream))
        val vertB = vertecies(readInt(inputStream))
        val vertC = vertecies(readInt(inputStream))
        faces(i) = new Triangle(vertA, vertB, vertC)
        if (i % (faces.length / 10) == 0 && i > 0) {
          printf("%2d%%... ", i * 100 / faces.length)
        }
        i += 1
      }
      println
    } else {
      def readInt(s: String): Int = {
        val index = Integer.parseInt(s)
        if (index < 0) {
          throw new RuntimeException(s"Negative index $index found. File must be corrupt.")
        }
        if (index >= vertecies.length) {
          throw new RuntimeException(s"Index $index too large. File must be corrupt.")
        }
        return index
      }

      var i = 0
      while (i < faces.length) {
        val line = scanner.nextLine
        val segments = line.split(' ')
        val length = Integer.parseInt(segments(0))
        if (3 != length) {
          throw new RuntimeException("Can't handle faces with more than 3 vertecies.")
        }
        val vertA = vertecies(readInt(segments(1)))
        val vertB = vertecies(readInt(segments(2)))
        val vertC = vertecies(readInt(segments(3)))
        faces(i) = new Triangle(vertA, vertB, vertC)
        if (i % (faces.length / 10) == 0 && i > 0) {
          printf("%2d%%... ", i * 100 / faces.length)
        }
        i += 1
      }
      println
    }
    println("import done")

    scanner.close

    return faces
  }
}