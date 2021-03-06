# Scala-PT
A pathtracer written in [Scala](http://scala-lang.org/) for rendering somewhat realistic images.

This project was originally inspired by https://github.com/Harha/JPath. I read through the source of one of the first commits and then decided to try writing a pathtracer myself.

# Pretty Example Pictures!

[![20*20 colorful glass spheres](https://dl.dropboxusercontent.com/u/21098557/krams/ray85m.png)](https://dl.dropboxusercontent.com/u/21098557/krams/ray85.png)
colorful glass spheres

[![10*10 red glass spheres with varying light absorbtion and refractivity index](https://dl.dropboxusercontent.com/u/21098557/krams/ray84m.png)](https://dl.dropboxusercontent.com/u/21098557/krams/ray84.png)
red glass spheres with varying light absorbtion and refractivity index

[![colored lights adding up to create neutral lighting](https://dl.dropboxusercontent.com/u/21098557/krams/ray63m.png)](https://dl.dropboxusercontent.com/u/21098557/krams/ray63.png)
colored lights adding up to create neutral lighting

[![The Stanford Dragon with subsurface scattering and refractive surface](https://dl.dropboxusercontent.com/u/21098557/krams/ray73m.png)](https://dl.dropboxusercontent.com/u/21098557/krams/ray73.png)
The [Stanford Dragon](http://www.cc.gatech.edu/projects/large_models/dragon.html) with subsurface scattering and refractive surface

[![A rendering of the Power 8 Mandelbulb. This took ages to render...](https://dl.dropboxusercontent.com/u/21098557/krams/ray94m.png)](https://dl.dropboxusercontent.com/u/21098557/krams/ray94.png)
A rendering of the Power 8 Mandelbulb. This took ages to render...

# Features
A probably incomplete list of features this pathtracer currently supports:
* Rendering:
  * Distance based light absorbtion in volumes
  * Scattering of light rays in participating media (fog)
  * Refraction and reflection of light on surfaces according to the refractivity indices
  * Sky material for rays that hit no geometry
* Materials:
  * Fully Diffuse
  * Reflective material with optional roughness
  * Refractive material with roughness parameter 
  * transparent material with light absorbtion and ray scattering
  * simple light emitting material
  * directional light emitting material
  * delegating material that chooses between two different materials based on the location in space
* Basic Shapes:
  * Trangles
  * AABBs
  * Spheres
  * raymarched (depth estimated) shapes
    * Sphere
    * Mandelbulb
* Other:
  * Depth of Field effect controlled by aperture size and focus length
  * the programm will try to use all CPU cores for rendering
  * the scene is stored in a BVH during rendering
  * simple and incomplete importer for PLY model files

# Plans
Plans are currently found in [this file](https://github.com/Kaloffl/Scala-PT/blob/master/Code/SPath/plans.txt). I will go through that list and put a redone version here later.

# Links
Here is an unordered list of resources I came across while researching for this project. If you are interested in ray/pathtracing you might want to check them out.
* https://github.com/TomCrypto/Lambda
* https://github.com/JamesGriffin/Path-Tracer
* https://github.com/Harha/JPath
* https://github.com/diwi/Space_Partitioning_Octree_BVH
* https://graphics.stanford.edu/papers/veach_thesis/thesis.pdf
* http://www.cc.gatech.edu/projects/large_models/
* http://www.woo4.me/
* http://www.luxrender.net/wiki/LuxRender_Volumes
* http://blog.hvidtfeldts.net/index.php/2015/01/path-tracing-3d-fractals/
