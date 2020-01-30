package com.minosiants
package youtube.extra

import java.time.Instant

import cats.effect.IO
import cats.syntax.either._
import io.circe.{ Decoder, Encoder }
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import io.circe.generic.auto._

case class YoutubeDataThumbnail(url: String, width: Int, height: Int)
case class YoutubeDataThumbnails(
    default: YoutubeDataThumbnail,
    medium: YoutubeDataThumbnail,
    high: YoutubeDataThumbnail,
    standard: YoutubeDataThumbnail
)

case class YoutubeDataResourceId(kind: String, videoId: String)
case class YoutubeDataSnippet(
    resourceId: YoutubeDataResourceId,
    channelTitle: String,
    playlistId: String,
    publishedAt: Instant,
    channelId: String,
    title: String,
    description: String,
    thumbnails: Option[YoutubeDataThumbnails]
)
case class YoutubeDataItem(id: String, snippet: YoutubeDataSnippet)
case class YoutubeDataPageInfo(totalResults: Int, resultsPerPage: Int)
case class YoutubeDataPlaylistItems(
    kind: String,
    nextPageToken: Option[String],
    prevPageToken: Option[String],
    pageInfo: YoutubeDataPageInfo,
    items: List[YoutubeDataItem]
)

case class YoutubeDataVideoStatistics(viewCount: String, likeCount: String, dislikeCount: String, favoriteCount: String)
case class YoutubeDataVideo(id: String, statistics: YoutubeDataVideoStatistics)
case class YoutubeDataVideos(pageInfo: YoutubeDataPageInfo, items: List[YoutubeDataVideo])

object YoutubeDataVideos {
  implicit def decoder: EntityDecoder[IO, YoutubeDataVideos] = jsonOf[IO, YoutubeDataVideos]
}

object YoutubeDataPlaylistItems {
  implicit val encodeInstant: Encoder[Instant] = Encoder.encodeString.contramap[Instant](_.toString)
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(Instant.parse(str)).leftMap(_.toString)
  }

  implicit def decoder: EntityDecoder[IO, YoutubeDataPlaylistItems] = jsonOf[IO, YoutubeDataPlaylistItems]

}
