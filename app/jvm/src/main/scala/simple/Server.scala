package simple

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, CacheDirectives, `Cache-Control`}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties

object Server{
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val port = Properties.envOrElse("PORT", "8080").toInt
    val route = {
      get {
        pathSingleSlash{
          complete{
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              "<!DOCTYPE html>" + Page.skeleton.render
            )
          }
        } ~
        getFromResourceDirectory("") ~
        path("token") {
          complete {
            Http().singleRequest(HttpRequest(
              method = HttpMethods.POST,
              uri = "https://accounts.spotify.com/api/token",
              headers = List(Authorization(BasicHttpCredentials("748b966f5afc4831939ec6516332bbe6", "899959dceebf41f78ae342206d39266a"))),
              entity = FormData("grant_type" -> "client_credentials").toEntity
            )).map { response =>
              response.withHeaders(`Cache-Control`(CacheDirectives.`max-age`(3600)))
            }
          }
        }
      }

      // ~
//      post{
//        path("ajax" / "list"){
//          entity(as[String]) { e =>
//            complete {
//              upickle.default.write(list(e))
//            }
//          }
//        }
//      }
    }
    Http().bindAndHandle(route, "0.0.0.0", port = port)
  }
//  def list(path: String) = {
//    val (dir, last) = path.splitAt(path.lastIndexOf("/") + 1)
//    val files =
//      Option(new java.io.File("./" + dir).listFiles())
//        .toSeq.flatten
//    for{
//      f <- files
//      if f.getName.startsWith(last)
//    } yield FileData(f.getName, f.length())
//  }
}
