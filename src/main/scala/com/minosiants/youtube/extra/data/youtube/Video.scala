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
    thumbnails: Option[Thumbnails]
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

  val textFieldsTrav =
    Traversal
      .apply3[VideoSnippet, String](_.channelTitle, _.title, _.description) {
        case (ch, t, d, l) =>
          l.copy(channelTitle = ch, title = t, description = d)
      }

}
