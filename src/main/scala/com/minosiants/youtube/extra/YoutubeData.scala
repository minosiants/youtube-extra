package com.minosiants
package youtube.extra

import java.time.Instant

import cats.effect.IO
import cats.syntax.either._
import io.circe.{ Decoder, Encoder }
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import io.circe.generic.auto._

sealed trait YoutubeDataThumbnail {
  def url: String
  def width: Int
  def height: Int

}
case class YoutubeDataDefault(url: String, width: Int, height: Int)  extends YoutubeDataThumbnail
case class YoutubeDataMedium(url: String, width: Int, height: Int)   extends YoutubeDataThumbnail
case class YoutubeDataHigh(url: String, width: Int, height: Int)     extends YoutubeDataThumbnail
case class YoutubeDataStandard(url: String, width: Int, height: Int) extends YoutubeDataThumbnail

object YoutubeDataThumbnail {}

case class YoutubeDataResourceId(kind: String, videoId: String)
case class YoutubeDataSnippet(
    resourceId: YoutubeDataResourceId,
    channelTitle: String,
    playlistId: String,
    publishedAt: Instant,
    channelId: String,
    title: String,
    description: String //,
    //thumbnails: List[YoutubeDataThumbnail]
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

object YoutubeDataPlaylistItems {
  implicit val encodeInstant: Encoder[Instant] = Encoder.encodeString.contramap[Instant](_.toString)
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(Instant.parse(str)).leftMap(_.toString)
  }
  implicit def decoder: EntityDecoder[IO, YoutubeDataPlaylistItems] = jsonOf[IO, YoutubeDataPlaylistItems]
}
