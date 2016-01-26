package kaloffl.spath.scene.shapes

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Ray
import kaloffl.spath.math.Vec3d

trait Projectable {

  /**
   * Returns the fraction that the object would take up when projected onto a 
   * unit sphere around the given point.
   */
  def getSolidAngle(point: Vec3d): Double

  /**
   * Creates a ray from the given point in the direction of this Projectable.
   * The random distribution should be based on the projected area and not on
   * the volume of the Projectable. For example: for a sphere the distribution
   * should be based on a flat circle, otherwise more samples would collect
   * near the center.
   */
  def createRandomRay(start: Vec3d, random: DoubleSupplier): Ray
}