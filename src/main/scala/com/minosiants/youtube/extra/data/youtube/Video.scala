package com.minosiants.youtube.extra.data.youtube

import java.time.Instant

import monocle.Traversal
import monocle.macros.GenLens

final case class VideoSnippet(
    channelTitle: String,
    publishedAt: Instant,
    channelId: String,
    title: String,
    description: String,
    thumbnails: Option[Thumbnails],
    tags: Option[List[String]]
)

final case class VideoStatistics(
    viewCount: Option[String],
    likeCount: Option[String],
    dislikeCount: Option[String],
    favoriteCount: String,
    commentCount: Option[String]
)
final case class VideoContentDetails(
    duration: String,
    dimension: String
)

final case class Video(
    id: String,
    snippet: VideoSnippet,
    contentDetails: VideoContentDetails,
    statistics: VideoStatistics
)

object Video {

  val snippetLens = GenLens[Video](_.snippet)

  val titleAndDescriptionTrav =
    Traversal.apply2[VideoSnippet, String](_.title, _.description) {
      case (t, d, l) => l.copy(title = t, description = d)
    }

}
