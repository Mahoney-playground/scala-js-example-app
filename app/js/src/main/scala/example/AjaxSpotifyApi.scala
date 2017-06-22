package example

import fr.hmil.roshttp.{HttpRequest, Method}
import fr.hmil.roshttp.response.SimpleHttpResponse

import scala.concurrent.Future
import scala.scalajs.js.{JSON, URIUtils}
import monix.execution.Scheduler.Implicits.global

import scala.scalajs.js

object AjaxSpotifyApi {

  def apply() = new AjaxSpotifyApi(new AuthorisingClient(SimpleClient, (refresh: Boolean) => {
    val tokenRequest = HttpRequest("http://localhost:8080/token")
    val request = if (refresh) tokenRequest.withHeader("Cache-Control", "no-cache") else tokenRequest
    SimpleClient.execute(request).map { resp =>
      val tokenResponse = JSON.parse(resp.body).asInstanceOf[TokenResponse]
      s"${tokenResponse.token_type} ${tokenResponse.access_token}"
    }
  }))
}

class AjaxSpotifyApi private (httpClient: HttpClient) extends SpotifyApi {

  def fetchArtist(name: String): Future[Option[Artist]] = {
    get(artistSearchURL(name)) map { xhr =>
      val searchResults = JSON.parse(xhr.body).asInstanceOf[SearchResults]
      searchResults.artists.items.headOption
    }
  }

  def fetchAlbums(artistId: String): Future[Seq[Album]] = {
    get(albumsURL(artistId)) map { xhr =>
      val albumListing = JSON.parse(xhr.body).asInstanceOf[ItemListing[Album]]
      albumListing.items
    }
  }


  def fetchTracks(albumId: String): Future[Seq[Track]] = {
    get(tracksURL(albumId)) map { xhr =>
      val trackListing = JSON.parse(xhr.body).asInstanceOf[ItemListing[Track]]
      trackListing.items
    }
  }

  private def get(url: String): Future[SimpleHttpResponse] = {
    httpClient.execute(HttpRequest(url))
  }

  def artistSearchURL(name: String) = s"https://api.spotify.com/v1/search?type=artist&market=GB&q=${URIUtils.encodeURIComponent(name)}"
  def albumsURL(artistId: String) =   s"https://api.spotify.com/v1/artists/$artistId/albums?limit=50&market=GB&album_type=album"
  def tracksURL(albumId: String) =    s"https://api.spotify.com/v1/albums/$albumId/tracks?limit=50&market=GB"
}

trait HttpClient {

  def execute(httpRequest: HttpRequest): Future[SimpleHttpResponse]

}

class AuthorisingClient(
  decorated: HttpClient = SimpleClient,
  authoriser: (Boolean) => Future[String]
) extends HttpClient {

  def execute(httpRequest: HttpRequest): Future[SimpleHttpResponse] = {

    val result = executeWithAuth(httpRequest, refreshAuth = false)

    result.flatMap { resp =>
      if (resp.statusCode == 401) {
        executeWithAuth(httpRequest, refreshAuth = true)
      } else {
        Future(resp)
      }
    }
  }

  private def executeWithAuth(httpRequest: HttpRequest, refreshAuth: Boolean) = {
    authoriser(refreshAuth).flatMap { authHeader =>
      decorated.execute(httpRequest.withHeader("Authorization", authHeader))
    }
  }
}

object SimpleClient extends HttpClient  {

  def execute(httpRequest: HttpRequest): Future[SimpleHttpResponse] = httpRequest.send()
}

@js.native
trait TokenResponse extends js.Object {
  def access_token: String
  def token_type: String
  def expires_in: Int
}
