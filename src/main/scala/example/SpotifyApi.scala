package example

import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import scala.scalajs.js.{JSON, URIUtils}
import scala.concurrent.ExecutionContext.Implicits.global

object SpotifyApi {

  private val token = "BQBeD1Dh5TgTRxIUSbraxNxybOjYTU05WL_vL1OnrNj3Jl0kTf3S9FYu5KFYUo5gqtiyL-K_912m8kdXQmuEUQ"
  private val authHeader = "Authorization" -> s"Bearer $token"

  def fetchArtist(name: String): Future[Option[Artist]] = {
    get(artistSearchURL(name)) map { xhr =>
      val searchResults = JSON.parse(xhr.responseText).asInstanceOf[SearchResults]
      searchResults.artists.items.headOption
    }
  }

  def fetchAlbums(artistId: String): Future[Seq[Album]] = {
    get(albumsURL(artistId)) map { xhr =>
      val albumListing = JSON.parse(xhr.responseText).asInstanceOf[ItemListing[Album]]
      albumListing.items
    }
  }


  def fetchTracks(albumId: String): Future[Seq[Track]] = {
    get(tracksURL(albumId)) map { xhr =>
      val trackListing = JSON.parse(xhr.responseText).asInstanceOf[ItemListing[Track]]
      trackListing.items
    }
  }

  private def get(url: String): Future[XMLHttpRequest] = {
    Ajax.get(url, null, 0, Map(authHeader))
  }

  def artistSearchURL(name: String) = s"https://api.spotify.com/v1/search?type=artist&q=${URIUtils.encodeURIComponent(name)}"
  def albumsURL(artistId: String) =   s"https://api.spotify.com/v1/artists/$artistId/albums?limit=50&market=PT&album_type=album"
  def tracksURL(albumId: String) =    s"https://api.spotify.com/v1/albums/$albumId/tracks?limit=50"
}
