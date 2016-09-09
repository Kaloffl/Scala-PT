package kaloffl.spath.scene.shapes

import java.util.function.DoubleSupplier

import kaloffl.spath.math.Vec3d

/**
  * Created by Lars on 02.09.2016.
  */
trait Emitter {

  def getRandomPointOfSurface(random: DoubleSupplier): Vec3d
}
