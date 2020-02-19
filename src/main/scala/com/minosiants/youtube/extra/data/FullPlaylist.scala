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

  val videoTitleAndDescriptionLens = (allVideos composeLens Video.snippetLens composeTraversal Video.titleAndDescriptionTrav)

  val playlistLens        = GenLens[FullPlaylist](_.playlistInfo)
  val playlistSnippetLens = GenLens[Playlist](_.snippet)
  val playlisttitleAndDescription =
    Traversal
      .apply2[PlaylistSnippet, String](_.title, _.description) {
        case (fn, ln, l) => l.copy(title = fn, description = ln)
      }

  val playlistTitleAndDescriptionLens = (playlistLens composeLens playlistSnippetLens composeTraversal playlisttitleAndDescription)
}
