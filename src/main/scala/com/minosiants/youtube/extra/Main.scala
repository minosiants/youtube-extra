package com.minosiants
package youtube.extra

import cats.effect._
import cats.implicits._
import org.http4s.client.blaze._
//import org.http4s.Uri._
import cats.effect.{ ContextShift, IO }

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {

  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeClientBuilder[IO](global).resource
      .use { client =>
        YoutubeDataClient(client, YoutubeDataAccessProps("", "")).getPlayList("")
      }
      .as(ExitCode.Success)

  }
}
