package com.minosiants.youtube.extra.data

import com.minosiants.youtube.extra.data.youtube.{
  Codecs,
  Playlist,
  PlaylistSnippet,
  Video
}
import monocle.Traversal
import monocle.function.all._
import monocle.macros.GenLens

final case class FullPlaylist(
    playlistInfo: Playlist,
    videos: List[Video]
)

object FullPlaylist extends Codecs {

  val videosLens = GenLens[FullPlaylist](_.videos)

  val allVideos
      : Traversal[FullPlaylist, Video] = videosLens composeTraversal each

  val videoTextFiledsLens = (allVideos composeLens Video.snippetLens composeTraversal Video.textFieldsTrav)

  val playlistLens        = GenLens[FullPlaylist](_.playlistInfo)
  val playlistSnippetLens = GenLens[Playlist](_.snippet)
  val playlistTextFieldsTrav =
    Traversal
      .apply3[PlaylistSnippet, String](_.channelTitle, _.title, _.description) {
        case (ch, t, d, l) =>
          l.copy(channelTitle = ch, title = t, description = d)
      }

  val playlistTextFieldsLens = (playlistLens composeLens playlistSnippetLens composeTraversal playlistTextFieldsTrav)
}
