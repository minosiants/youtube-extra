package com.minosiants
package youtube.extra

import cats.effect._
import cats.implicits._
import org.http4s.Uri
import org.http4s.client.blaze._
//import org.http4s.Uri._
import cats.effect.{ ContextShift, IO }

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {

  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  val baseUri = Uri.unsafeFromString("https://www.googleapis.com")
  val key =""
  val token = ""

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeClientBuilder[IO](global).resource
      .use { client =>
        YoutubeDataClient(client, baseUri, YoutubeDataAccessProps(key, token)).getPlayList("")
      }
      .as(ExitCode.Success)

  }
}
