package kaloffl.spath.importer

import kaloffl.spath.scene.shapes.Shape
import java.io.File
import java.io.FileInputStream
import java.util.Scanner
import kaloffl.spath.math.Vec3d
import kaloffl.spath.scene.shapes.Triangle

/**
 * @author Lars
 */
object PlyImporter {

  def load(file: String, scale: Vec3d = Vec3d.UNIT, offset: Vec3d = Vec3d.ORIGIN): Array[Shape] = {

    val scanner = new Scanner(new FileInputStream(file))

    var vertecies: Array[Vec3d] = null
    var faces: Array[Shape] = null

    println("Parsing header")
    var header = true
    while (header && scanner.hasNextLine()) {
      val line = scanner.nextLine
      val segments = line.split(' ')

      if (segments(0) equals "element") {
        if (segments(1) equals "vertex") {
          val vertCount = Integer.parseInt(segments(2))
          println("Vertecies: " + vertCount)
          vertecies = new Array(vertCount)
        } else if (segments(1) equals "face") {
          val faceCount = Integer.parseInt(segments(2))
          println("faces: " + faceCount)
          faces = new Array(faceCount)
        }
      } else if (segments(0) equals ("end_header")) {
        header = false
      }
    }

    println("Reading vertecies")
    for (i ← 0 until vertecies.length) {
      val line = scanner.nextLine
      val segments = line.split(' ')
      val x = java.lang.Double.parseDouble(segments(0)) * scale.x + offset.x
      val y = java.lang.Double.parseDouble(segments(1)) * scale.y + offset.y
      val z = java.lang.Double.parseDouble(segments(2)) * scale.z + offset.z
      vertecies(i) = Vec3d(x, y, z)
    }

    println("Reading faces")
    for (i ← 0 until faces.length) {
      val line = scanner.nextLine
      val segments = line.split(' ')
      if (segments.length == 4) {
        val vertA = vertecies(Integer.parseInt(segments(1)))
        val vertB = vertecies(Integer.parseInt(segments(2)))
        val vertC = vertecies(Integer.parseInt(segments(3)))
        faces(i) = new Triangle(vertA, vertB, vertC)
      } else {
        throw new UnsupportedOperationException("Face must have 3 verts!")
      }
    }
    println("import done")

    scanner.close

    return faces
  }
}