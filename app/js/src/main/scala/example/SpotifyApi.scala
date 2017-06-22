package example

import scala.concurrent.Future

trait SpotifyApi {

  def fetchArtist(name: String): Future[Option[Artist]]

  def fetchAlbums(artistId: String): Future[Seq[Album]]

  def fetchTracks(albumId: String): Future[Seq[Track]]
}
