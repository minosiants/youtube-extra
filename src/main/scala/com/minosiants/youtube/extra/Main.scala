package com.minosiants
package youtube.extra

import cats._
import cats.effect._
import cats.implicits._
import io.circe.Decoder
import org.http4s.client.blaze._
import org.http4s.Uri
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.headers._
//import org.http4s.Uri._
import org.http4s.client._
import scala.concurrent.ExecutionContext.global
import cats.effect.IO
import cats.effect.ContextShift
import org.http4s.circe._
import io.circe.generic.auto._

object Main extends IOApp {



  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  def playListItemsUrl(apiKey: String, playlistId: String): Uri = {
    Uri
      .unsafeFromString("https://www.googleapis.com/youtube/v3/playlistItems")
      .withQueryParam("key", apiKey)
      .withQueryParam("playlistId", playlistId)
      .withQueryParam("part", "snippet")
  }

  def getPlayList(client: Client[IO]) = IO {
    implicit def decoder: EntityDecoder[IO, GoogleDataPlaylistItems] = jsonOf[IO,GoogleDataPlaylistItems ]
    val request = Method.GET(
      playListItemsUrl("AIzaSyAa8yy0GdcGPHdtD083HiGGx_S0vMPScDM", "PLBCF2DAC6FFB574DE"),
      Authorization(
        Credentials.Token(
          AuthScheme.Bearer,
          "ya29.ImS7B6WJQX-IpWOFWhQABVBnDA9mAVjJpBk8MKPfyt59tAltnMxf7GNKeBkpyqhSTaFo409_OgHD60p3PlzH_8gKnwEOsVwEYAVmcbAs-NxS5WVHs8I37eh-jM7rCOH0wc_wzSgt"
        )
      ),
      Accept(MediaType.application.json),
      Header("x-origin", "https://explorer.apis.google.com")
    )

    println(request)
    val res = client.expect[GoogleDataPlaylistItems](request)

    println(res.unsafeRunSync())

  }

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeClientBuilder[IO](global).resource.use(getPlayList).as(ExitCode.Success)

  }
}
