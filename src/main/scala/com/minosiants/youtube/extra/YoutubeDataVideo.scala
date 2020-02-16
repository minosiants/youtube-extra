package com.minosiants
package youtube.extra

import java.time.Instant

import monocle.Traversal
import monocle.macros.GenLens

final case class YoutubeDataVideoSnippet(
    channelTitle: String,
    publishedAt: Instant,
    channelId: String,
    title: String,
    description: String,
    thumbnails: Option[YoutubeDataThumbnails],
    tags: Option[List[String]]
)
final case class YoutubeDataVideoStatistics(
    viewCount: String,
    likeCount: Option[String],
    dislikeCount: Option[String],
    favoriteCount: String,
    commentCount: Option[String]
)
final case class YoutubeDataVideoContentDetails(
    duration: String,
    dimension: String
)

final case class YoutubeDataVideo(
    id: String,
    snippet: YoutubeDataVideoSnippet,
    contentDetails: YoutubeDataVideoContentDetails,
    statistics: YoutubeDataVideoStatistics
)

object YoutubeDataVideo extends CommonCodecs {

  val itemsLens   = GenLens[GoogleDataPage[YoutubeDataVideo]](_.items)
  val snippetLens = GenLens[YoutubeDataVideo](_.snippet)

  val allVideos
      : Traversal[GoogleDataPage[YoutubeDataVideo], YoutubeDataVideo] = itemsLens composeTraversal each

  val titleAndDescription =
    Traversal.apply2[YoutubeDataVideoSnippet, String](_.title, _.description) {
      case (fn, ln, l) => l.copy(title = fn, description = ln)
    }
  val titleAndDescriptionLens = (snippetLens composeTraversal titleAndDescription)

}
