package kaloffl.spath.tracing

import kaloffl.spath.Display

class Context(
    val random: () ⇒ Double, 
    val passNum : Int,
    val maxBounces: Int,
    val display: Display)