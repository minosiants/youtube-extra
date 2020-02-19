package com.minosiants.youtube.extra.data

import com.minosiants.youtube.extra.data.youtube.{
  CommonCodecs,
  YoutubeDataPlaylist,
  YoutubeDataPlaylistSnippet,
  YoutubeDataVideo
}
import monocle.Traversal
import monocle.function.all._
import monocle.macros.GenLens

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
