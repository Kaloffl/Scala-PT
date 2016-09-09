package kaloffl.spath

import java.util.concurrent.ThreadLocalRandom
import java.util.function.DoubleSupplier

import kaloffl.jobs.{Job, JobPool}
import kaloffl.spath.filter.ScaleFilter
import kaloffl.spath.math.{Quaternion, Vec3d}
import kaloffl.spath.scene.{Scene, Viewpoint}
import kaloffl.spath.tracing.{Tracer, TracingWorker}

object RtApplication {

  val processors = Runtime.getRuntime.availableProcessors
  val numberOfWorkers = if (1 == processors) 1 else processors * processors * 4
  val rows = Math.sqrt(numberOfWorkers).toInt
  val cols = numberOfWorkers / rows

  println("worker threads: " + numberOfWorkers)

  private var stopped = false

  def stop(): Unit = {
    stopped = true
  }

  def run(
           target: RenderTarget,
           events: Iterator[InputEvent],
           tracer: Tracer,
           scene: Scene,
           initialView: Viewpoint) {

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
        events.next match {
          case KeyEvent(key, true) => {
            view = key match {
              case KeyEvent.Key_W => {
                reset = true
                new Viewpoint(view.position + view.forward * 0.1, view.forward, view.up)
              }
              case KeyEvent.Key_A => {
                reset = true
                new Viewpoint(view.position + view.right * -0.1, view.forward, view.up)
              }
              case KeyEvent.Key_S => {
                reset = true
                new Viewpoint(view.position + view.forward * -0.1, view.forward, view.up)
              }
              case KeyEvent.Key_D => {
                reset = true
                new Viewpoint(view.position + view.right * 0.1, view.forward, view.up)
              }

              case KeyEvent.Key_Q => {
                reset = true
                val quat = Quaternion(Vec3d.Up, Math.PI / 8)
                new Viewpoint(
                  view.position,
                  (quat * view.forward * ~quat).toVec3,
                  (quat * view.up * ~quat).toVec3)
              }
              case KeyEvent.Key_E => {
                reset = true
                val quat = Quaternion(Vec3d.Up, 15 * Math.PI / 8)
                new Viewpoint(
                  view.position,
                  (quat * view.forward * ~quat).toVec3,
                  (quat * view.up * ~quat).toVec3)
              }
              case KeyEvent.Key_R => {
                reset = true
                val quat = Quaternion(view.right, Math.PI / 8)
                new Viewpoint(
                  view.position,
                  (quat * view.forward * ~quat).toVec3,
                  (quat * view.up * ~quat).toVec3)
              }
              case KeyEvent.Key_F => {
                reset = true
                val quat = Quaternion(view.right, 15 * Math.PI / 8)
                new Viewpoint(
                  view.position,
                  (quat * view.forward * ~quat).toVec3,
                  (quat * view.up * ~quat).toVec3)
              }

              case _ => view
            }
          }
          case _ =>
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
          override def execute(): Unit = {
            worker.render(view)
            worker.draw()
          }
        })
      }
      pool.execute()

      val after = System.nanoTime
      val duration = after - before
      if (duration > 1000000000) {
        println("rendertime: " + Math.floor(duration / 10000000.0) / 100.0 + "s")
      } else {
        println("rendertime: " + Math.floor(duration / 10000.0) / 100.0 + "ms")
      }

      actualTarget.commit()
    }
  }
}