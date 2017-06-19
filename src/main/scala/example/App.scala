package example

import japgolly.scalajs.react.CtorType.Nullary
import japgolly.scalajs.react.component.Scala.Component
import japgolly.scalajs.react.vdom.TagOf
import japgolly.scalajs.react.{BackendScope, Callback, CallbackTo, ReactEventTypes, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^.^._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom
import org.scalajs.dom.html.Div

import scala.scalajs.js
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object App extends js.JSApp {

  def main(): Unit = {
    TrackListingApp.component().renderIntoDOM(dom.document.getElementById("playground"))
  }
}

object TrackListingApp {

  val component: Component[Unit, TrackListingState, TrackListingOps, Nullary] = ScalaComponent.builder[Unit]("Spotify Track Listing 1")
    .initialState(TrackListingState.empty)
    .renderBackend[TrackListingOps]
    .build

}

class TrackListingOps($: BackendScope[Unit, TrackListingState]) extends ReactEventTypes {

  private val artistInputState = $.zoomState(_.artistInput)(newArtistInput => _.copy(artistInput = newArtistInput))

  private def updateArtistInput(event: ReactEventFromInput) = artistInputState.setState(event.target.value)

  private val tracksState = $.zoomState(_.tracks)(newTracks => _.copy(tracks = newTracks))
  private val selectedAlbumState = $.zoomState(_.selectedAlbum)(newSelectedAlbum => _.copy(selectedAlbum = newSelectedAlbum))

  private def updateTracks(event: ReactEventFromInput) = Callback.future {
    val albumId = event.target.value

    SpotifyApi.fetchTracks(albumId).map { tracks => selectedAlbumState.setState(albumId) >> tracksState.setState(tracks) }
  }

  private def searchForArtist(name: String)(event: ReactEvent) = Callback.future {
    event.preventDefault()
    for {
      artistOpt <- SpotifyApi.fetchArtist(name)
      albums <- artistOpt.map (artist => SpotifyApi.fetchAlbums(artist.id)) getOrElse Future.successful(Nil)
      tracks <- albums.find { _.id == $.state.runNow().selectedAlbum }.orElse(albums.headOption).map (album => SpotifyApi.fetchTracks(album.id)) getOrElse Future.successful(Nil)
    } yield {
      artistOpt match {
        case None => Callback.alert("No artist found")
        case Some(artist) => $.modState(s => s.copy(artistInput = artist.name, albums = albums, tracks = tracks))
      }
    }
  }

  def formatDuration(duration_ms: Int): String = {
    s"${duration_ms.toString} millis"
  }

  def render(s: TrackListingState): TagOf[Div] = {
    <.div(cls := "container",
      <.h1("Spotify Track Listing"),
      <.form(cls := "form-group", onSubmit ==> searchForArtist(s.artistInput),
        <.label(`for` := "artist", "Artist"),
        <.div(cls := "row", id := "artist",
          <.div(cls := "col-xs-10",
            <.input(
              `type` := "text",
              cls := "form-control",
              value := s.artistInput,
              onChange ==> updateArtistInput
            )
          ),
          <.div(cls := "col-xs-2",
            <.button(
              `type` := "submit",
              cls := "btn btn-primary custom-button-width",
              disabled := s.artistInput.isEmpty,
              "Search"
            )
          )
        )
      ),
      <.div(cls := "form-group",
        <.label(`for` := "album", "Album"),
        <.select(cls := "form-control", id := "album", value := s.selectedAlbum, onChange ==> updateTracks,
          s.albums.map { album =>
            <.option(value := album.id, album.name)
          }.toTagMod
        )
      ),
      <.hr,
      <.ul(s.tracks.map { track =>
        <.li(
          <.div(
            <.p(s"${track.track_number}. ${track.name} (${formatDuration(track.duration_ms)})"),
            <.audio(controls := true, key := track.preview_url,
              <.source(src := track.preview_url)
            )
          )
        )
      }.toTagMod)
    )
  }
}