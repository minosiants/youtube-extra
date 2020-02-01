package com.minosiants
package youtube.extra

import java.time.Instant

import cats.effect.IO
import cats.syntax.either._
import io.circe.{Decoder, Encoder}
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import io.circe.generic.auto._
import monocle.macros.GenLens
import monocle.function.all._
import monocle.Traversal

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
    thumbnails: Option[YoutubeDataThumbnails]
)
case class YoutubeDataItem(id: String, snippet: YoutubeDataSnippet) {
  def notPrivate: Boolean = snippet.thumbnails.nonEmpty
}
case class YoutubeDataPageInfo(totalResults: Int, resultsPerPage: Int)

case class YoutubeDataPlaylistItems(
    kind: String,
    nextPageToken: Option[String],
    prevPageToken: Option[String],
    pageInfo: YoutubeDataPageInfo,
    items: List[YoutubeDataItem]
)

case class YoutubeDataVideoSnippet(
    channelTitle: String,
    publishedAt: Instant,
    channelId: String,
    title: String,
    description: String,
    thumbnails: Option[YoutubeDataThumbnails],
    tags: List[String]
)
case class YoutubeDataVideoStatistics(
    viewCount: String,
    likeCount: String,
    dislikeCount: String,
    favoriteCount: String
)
case class YoutubeDataVideo(
    id: String,
    snippet: YoutubeDataVideoSnippet,
    statistics: YoutubeDataVideoStatistics
)
case class YoutubeDataVideos(
    pageInfo: YoutubeDataPageInfo,
    items: List[YoutubeDataVideo]
)

object YoutubeDataVideos extends CommonCodecs {
  implicit def decoder: EntityDecoder[IO, YoutubeDataVideos] =
    jsonOf[IO, YoutubeDataVideos]

  val itemsLens = GenLens[YoutubeDataVideos](_.items)
  val snippetLens = GenLens[YoutubeDataVideo](_.snippet)

  val allVideos: Traversal[YoutubeDataVideos, YoutubeDataVideo] = itemsLens composeTraversal each

  val titleAndDescription = Traversal.apply2[YoutubeDataVideoSnippet, String](_.title, _.description){ case (fn, ln, l) => l.copy(title = fn, description = ln)}
  val titleAndDescriptionLens = (allVideos composeLens snippetLens composeTraversal titleAndDescription)

}

object YoutubeDataPlaylistItems extends CommonCodecs {

  implicit def decoder: EntityDecoder[IO, YoutubeDataPlaylistItems] =
    jsonOf[IO, YoutubeDataPlaylistItems]

}

trait CommonCodecs {
  implicit val encodeInstant: Encoder[Instant] =
    Encoder.encodeString.contramap[Instant](_.toString)
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emap {
    str =>
      Either.catchNonFatal(Instant.parse(str)).leftMap(_.toString)
  }
}
