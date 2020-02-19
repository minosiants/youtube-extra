package com.minosiants.youtube.extra.data
package youtube

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

final case class PageInfo(totalResults: Int, resultsPerPage: Int)

case class Page[A](
    nextPageToken: Option[String],
    prevPageToken: Option[String],
    pageInfo: Option[PageInfo],
    items: List[A]
)

object Page extends Codecs {

  implicit def itemDecoder: EntityDecoder[IO, Page[Item]] =
    jsonOf[IO, Page[Item]]

  implicit def videoDecoder: EntityDecoder[IO, Page[Video]] =
    jsonOf[IO, Page[Video]]

  implicit def playlistDecoder: EntityDecoder[IO, Page[Playlist]] =
    jsonOf[IO, Page[Playlist]]

  implicit def subscriptionDecoder: EntityDecoder[IO, Page[Subscription]] =
    jsonOf[IO, Page[Subscription]]

  implicit def channelDecoder: EntityDecoder[IO, Page[Channel]] =
    jsonOf[IO, Page[Channel]]

}
