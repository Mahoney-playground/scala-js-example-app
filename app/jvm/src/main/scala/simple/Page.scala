package simple
import scalatags.Text.all._

object Page{

  private val boot = "example.App().main()"
  private val title = tag("title")
  private val integrity = attr("integrity")
  private val crossorigin = attr("crossorigin")

  val skeleton =
    html(
      head(
        title("Example Scala.js application"),
        meta(httpEquiv:="Content-Type", content:="text/html; charset=UTF-8"),
        meta(name:="viewport", content:="width=device-width, initial-scale=1"),
        link(rel:="stylesheet", href:="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css", integrity:="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u", crossorigin:="anonymous"),
        script(`type`:="text/javascript", src:="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"),
        script(`type`:="text/javascript", src:="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"),
        script(`type`:="text/javascript", src:="/app-jsdeps.js"),
        script(`type`:="text/javascript", src:="/app-fastopt.js")
      ),
      body(onload:=boot)(
        div(`class`:="app-container", id:="playground"),
        script(`type`:="text/javascript", src:="http://localhost:12345/workbench.js")
      )
    )
}
