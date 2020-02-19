package com.minosiants.youtube.extra.data.youtube

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

object YoutubeDataVideo {

  val snippetLens = GenLens[YoutubeDataVideo](_.snippet)

  val titleAndDescriptionTrav =
    Traversal.apply2[YoutubeDataVideoSnippet, String](_.title, _.description) {
      case (t, d, l) => l.copy(title = t, description = d)
    }

}
