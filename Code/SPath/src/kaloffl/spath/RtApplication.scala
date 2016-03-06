package kaloffl.spath

import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.locks.LockSupport
import java.util.function.DoubleSupplier
import kaloffl.jobs.Job
import kaloffl.jobs.JobPool
import kaloffl.spath.scene.Scene
import kaloffl.spath.tracing.Tracer
import kaloffl.spath.tracing.TracingWorker
import kaloffl.spath.scene.Viewpoint
import kaloffl.spath.math.Quaternion
import kaloffl.spath.filter.ScaleFilter

object RtApplication {

  val processors = Runtime.getRuntime.availableProcessors
  val numberOfWorkers = if (1 == processors) 1 else processors * processors * 4
  val rows = Math.sqrt(numberOfWorkers).toInt
  val cols = numberOfWorkers / rows

  println("worker threads: " + numberOfWorkers)

  private var stopped = false

  def stop: Unit = {
    stopped = true
  }

  def run(target: RenderTarget, events: Iterator[InputEvent], tracer: Tracer, scene: Scene, initialView: Viewpoint, bounces: Int = 8) {

    val tracingWorkers = new Array[TracingWorker](numberOfWorkers)

    val random = new DoubleSupplier {
      override def getAsDouble = ThreadLocalRandom.current.nextDouble
    }

    var reset = true
    var view = initialView
    var actualTarget = target
    val pool = new JobPool
    while (!stopped) {
      while (events.hasNext) {
        val event = events.next
        if (event.pressed) {
          view = event.key match {
            case InputEvent.Key_W => new Viewpoint(view.position + view.forward * 0.1, view.forward, view.up)
            case InputEvent.Key_A => new Viewpoint(view.position + view.right * -0.1, view.forward, view.up)
            case InputEvent.Key_S => new Viewpoint(view.position + view.forward * -0.1, view.forward, view.up)
            case InputEvent.Key_D => new Viewpoint(view.position + view.right * 0.1, view.forward, view.up)

            case InputEvent.Key_Q => {
              val quat = Quaternion(view.up, Math.PI / 8)
              new Viewpoint(view.position, (quat * view.forward * ~quat).toVec3, view.up)
            }
            case InputEvent.Key_E => {
              val quat = Quaternion(view.up, 15 * Math.PI / 8)
              new Viewpoint(view.position, (quat * view.forward * ~quat).toVec3, view.up)
            }
            case InputEvent.Key_R => {
              val quat = Quaternion(view.right, Math.PI / 8)
              val iquat = ~quat
              val forward = (quat * view.forward * iquat).toVec3
              val up = (quat * view.up * iquat).toVec3
              new Viewpoint(view.position, forward, up)
            }
            case InputEvent.Key_F => {
              val quat = Quaternion(view.right, 15 * Math.PI / 8)
              val iquat = ~quat
              val forward = (quat * view.forward * iquat).toVec3
              val up = (quat * view.up * iquat).toVec3
              new Viewpoint(view.position, forward, up)
            }

            case _ => view
          }
          reset = true
        }
      }
      if (reset || target != actualTarget) {
        if (reset) {
          actualTarget = new ScaleFilter(target, 2, 2)
          reset = false
        } else {
          actualTarget = target
        }
        val tileWidth = actualTarget.width / cols
        val tileHeight = actualTarget.height / rows
        for (i ← 0 until numberOfWorkers) {
          val x = i % cols * tileWidth
          val y = i / cols * tileHeight
          val w = if ((i + 1) % cols == 0) actualTarget.width - x else tileWidth
          val h = if (i >= cols * (rows - 1)) actualTarget.height - y else tileHeight
          tracingWorkers(i) = new TracingWorker(x, y, w, h, tracer, scene, actualTarget, random)
        }
      }
      val before = System.nanoTime
      for (i ← 0 until numberOfWorkers) {
        val worker = tracingWorkers(i)
        pool.submit(new Job {
          override def execute = {
            worker.render(view, bounces, 0)
            worker.draw
          }
        })
      }
      pool.execute

      val after = System.nanoTime
      val duration = after - before
      if (duration > 1000000000) {
        println("rendertime: " + Math.floor(duration / 10000000.0) / 100.0 + "s")
      } else {
        println("rendertime: " + Math.floor(duration / 10000.0) / 100.0 + "ms")
      }

      actualTarget.commit
    }
  }
}