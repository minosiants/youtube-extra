package com.minosiants
package youtube.extra

import monocle.macros.GenLens
import monocle.function.all._
import monocle.Traversal

final case class FullPlaylist(
    playlistInfo: YoutubeDataPlaylist,
    videos: List[YoutubeDataVideo]
)

object FullPlaylist extends CommonCodecs {

  val videosLens = GenLens[FullPlaylist](_.videos)

  val allVideos
      : Traversal[FullPlaylist, YoutubeDataVideo] = videosLens composeTraversal each

  val videoTitleAndDescriptionLens = (allVideos composeLens YoutubeDataVideo.snippetLens composeTraversal YoutubeDataVideo.titleAndDescriptionTrav)

  val playlistLens        = GenLens[FullPlaylist](_.playlistInfo)
  val playlistSnippetLens = GenLens[YoutubeDataPlaylist](_.snippet)
  val playlisttitleAndDescription =
    Traversal
      .apply2[YoutubeDataPlaylistSnippet, String](_.title, _.description) {
        case (fn, ln, l) => l.copy(title = fn, description = ln)
      }

  val playlistTitleAndDescriptionLens = (playlistLens composeLens playlistSnippetLens composeTraversal playlisttitleAndDescription)
}
