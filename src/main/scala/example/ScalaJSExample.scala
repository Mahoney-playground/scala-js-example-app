package example

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.html.{Button, Div}
import org.scalajs.dom.raw.Element

import scala.scalajs.js.Date
import scala.util.Try
import scalatags.JsDom.all._

//@js.native
//trait EventName extends js.Object {
//  type EventType <: dom.Event
//}
//
//object EventName {
//  def apply[T <: dom.Event](name: String): EventName { type EventType = T } =
//    name.asInstanceOf[EventName { type EventType = T }]
//
//  val onmousedown = apply[dom.MouseEvent]("onmousedown")
//}
//
//@js.native
//trait ElementExt extends js.Object {
//  def addEventListener(name: EventName)(
//      f: js.Function1[name.EventType, _]): Unit
//}

object ScalaJSExample extends js.JSApp {

  private var thingsToDo = List(Task("Task1", 30), Task("Task2", 45))

  private val addDesc = input("New Task").render
  private val addTime = input("0").render
  private val addButton = button("Add a new task", `type`:="button", `class`:="btn btn-primary", marginTop:=10, marginBottom:=10).render
  private val timeSummary = div().render

  def main(): Unit = {

    val target = dom.document.getElementById("playground")

    updateUi()

    dom.window.setInterval(refreshTimeSummary(target), 600)

    addButton.onclick = (_: MouseEvent) => {
      val desc = addDesc.value
      val time = Try{ addTime.value.toInt }.toOption.getOrElse(0)
      thingsToDo = Task(desc, time) :: thingsToDo

      updateUi()
    }
  }

  /** Computes the square of an integer.
   *  This demonstrates unit testing.
   */
  def square(x: Int): Int = x*x

  def updateUi(): Unit = {
    val target = dom.document.getElementById("playground")
    target.innerHTML = ""
    target.appendChild(
      div(
        h1("Scala.js Organizer v2"),
        ul(
          thingsToDo.map(toListItem),
          `class` := "list-group"
        ),
        addForm,
        timeSummary,
        `class` := "col-sm-4"
      ).render
    )
  }

  private def toListItem(it: Task) = li(
    div(
      s"${it.desc} takes ${it.time} minutes",
      createDeleteButton(it)
    ),
    `class` := "list-group-item"
  )

  private def createDeleteButton(it: Task) = {
    val b = button("X", `class`:="btn btn-sm btn-danger", float.right, marginTop:= -5).render
    b.onclick = (_: MouseEvent) => {
      thingsToDo = thingsToDo.filterNot(_ == it)
      updateUi()
    }
    b
  }

  private val addForm = Array(
    div(addDesc, addTime),
    div(addButton)
  )

  private def format(d: Date) = s"${d.getHours()}:${d.getMinutes()}"
  private def refreshTimeSummary(target: Element) = () => {
    val timeNeeded: Int = thingsToDo.foldLeft(0)((x, y) => x + y.time)
    val now: Date = new Date
    val endDate = new Date(now.getTime() + timeNeeded*60000)

    timeSummary.innerHTML = s"${format(now)} + $timeNeeded minutes on tasks = ${format(endDate)}"
  }
}

case class Task(desc: String, time: Int)

