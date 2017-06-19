package example

import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import scala.scalajs.js.{JSON, URIUtils}
import scala.concurrent.ExecutionContext.Implicits.global

object SpotifyApi {

  private val token = "BQDvpgpyQvmFy8c1abF2A-M11BUlo16xrLOmehuqcJjJU2pBCdlsmO7waDhIIAY-a3PFmzdWLB8XhWvrqFE8yeo-jG-SJfzy6mjI4Trs7hFwQVSKmJvrl90UN7yIm_es4vF_x_rtBwl5FkqD7adODtbvu5qlZ8zSiH0urE1UwN4O"
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

  def artistSearchURL(name: String) = s"https://api.spotify.com/v1/search?type=artist&market=GB&q=${URIUtils.encodeURIComponent(name)}"
  def albumsURL(artistId: String) =   s"https://api.spotify.com/v1/artists/$artistId/albums?limit=50&market=GB&album_type=album"
  def tracksURL(albumId: String) =    s"https://api.spotify.com/v1/albums/$albumId/tracks?limit=50&market=GB"
}
