package com.minosiants
package youtube.extra

import java.time.Instant

import cats.effect.IO
import cats.syntax.either._
import io.circe.{ Decoder, Encoder }
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import io.circe.generic.auto._

final case class YoutubeDataThumbnail(
    url: String,
    width: Option[Int],
    height: Option[Int]
)

final case class YoutubeDataThumbnails(
    default: YoutubeDataThumbnail,
    medium: YoutubeDataThumbnail,
    high: YoutubeDataThumbnail,
    standard: Option[YoutubeDataThumbnail]
)

final case class YoutubeDataPageInfo(totalResults: Int, resultsPerPage: Int)

case class YoutubeDataPage[A](
    nextPageToken: Option[String],
    prevPageToken: Option[String],
    pageInfo: Option[YoutubeDataPageInfo],
    items: List[A]
)

object YoutubeDataPage extends CommonCodecs {
  implicit def itemDecoder
      : EntityDecoder[IO, YoutubeDataPage[YoutubeDataItem]] =
    jsonOf[IO, YoutubeDataPage[YoutubeDataItem]]

  implicit def videoDecoder
      : EntityDecoder[IO, YoutubeDataPage[YoutubeDataVideo]] =
    jsonOf[IO, YoutubeDataPage[YoutubeDataVideo]]

  implicit def playlistDecoder
      : EntityDecoder[IO, YoutubeDataPage[YoutubeDataPlaylist]] =
    jsonOf[IO, YoutubeDataPage[YoutubeDataPlaylist]]

  implicit def subscriptionDecoder
      : EntityDecoder[IO, YoutubeDataPage[YoutubeDataSubscription]] =
    jsonOf[IO, YoutubeDataPage[YoutubeDataSubscription]]

  implicit def channelDecoder
      : EntityDecoder[IO, YoutubeDataPage[YoutubeDataChannel]] =
    jsonOf[IO, YoutubeDataPage[YoutubeDataChannel]]

}

trait CommonCodecs {
  implicit val encodeInstant: Encoder[Instant] =
    Encoder.encodeString.contramap[Instant](_.toString)
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emap {
    str =>
      Either.catchNonFatal(Instant.parse(str)).leftMap(_.toString)
  }
}
