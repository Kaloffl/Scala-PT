package kaloffl.spath.math

class Transformation(scale: Vec3d = Vec3d.Unit,
                     rotation: Quaternion = Quaternion(1, 0, 0, 0),
                     translation: Vec3d = Vec3d.Origin) {

  val invRotation = ~rotation

  def transformRay(ray: Ray): Ray = {
    val pos = ray.start + translation
    val dir = transformDirection(ray.normal)
    val t = -pos.dot(dir)
    val p = pos + (dir * t)
    val o = (p * scale) - p

    new Ray(pos + o, dir)
  }

  def transformRayInverse(ray: Ray): Ray = {
    val pos = transformDirectionInverse(ray.start - translation)
    val dir = transformDirectionInverse(ray.normal)
    val t = -pos.dot(dir)
    val p = pos + (dir * t)
    val o = (p / scale) - p

    new Ray(pos + o, dir)
  }

  def transformPoint(point: Vec3d) = {
    point * scale + translation
  }

  def transformPointInverse(point: Vec3d) = {
    (point - translation) / scale
  }

  def transformDirection(direction: Vec3d) = {
        (rotation * direction * invRotation).toVec3
  }

  def transformDirectionInverse(direction: Vec3d) = {
        (invRotation * direction * rotation).toVec3
  }
}