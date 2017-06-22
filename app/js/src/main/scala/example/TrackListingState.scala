package example

object TrackListingState {
  val empty = TrackListingState("", Nil, "", Nil)
}

case class TrackListingState(
  artistInput: String,  // a text input for the artist name
  albums: Seq[Album],   // the list of albums to choose from
  selectedAlbum: String,
  tracks: Seq[Track]    // the list of tracks to display
)
