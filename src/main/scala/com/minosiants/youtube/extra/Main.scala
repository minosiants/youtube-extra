package com.minosiants
package youtube.extra

import java.io.File

import cats.effect._
import org.http4s.client.blaze._
import cats.effect.{ ContextShift, IO }
import cats.implicits._
import scala.concurrent.ExecutionContext.global
import PlaylistGenerator.createPlaylist
import YoutubeDataAccessProps.props
import YoutubeDataClient.{ apiUri, googleAppKey }
import Error._
object Main extends IOApp {

  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  def playlist(
      playlistId: String,
      token: String,
      destination: File
  ): IO[Unit] = {
    BlazeClientBuilder[IO](global).resource
      .use { client =>
        val ydClient =
          YoutubeDataClient(client, apiUri, props(googleAppKey, token))
        for {
          playlist <- ydClient.getFullPlaylist(playlistId)
          _        <- createPlaylist(playlist, destination)
        } yield ()
      }
  }

  def help(): IO[Unit] = IO {
    val message =
      """
        |youtube-extra: usage
        |
        |playlist -id -t (-d)
        |
        |    -id - playlist id
        |    -t  - OAuth token (can be obtained here: https://tinyurl.com/vltl9p6)
        |    -d  - optional directory where playlist will be saved. Default value is "."
        |
        |
        |""".stripMargin

    println(message)

  }
  override def run(args: List[String]): IO[ExitCode] = {

    CommandLineParser
      .parseArgs(args)
      .flatMap {
        case HelpCommand =>
          help()
        case PlaylistCommand(playlistId, token, Some(destination)) =>
          playlist(playlistId, token, destination)
        case PlaylistCommand(playlistId, token, None) =>
          playlist(playlistId, token, new File("."))
      }
      .attempt
      .flatMap {
        case Right(_) => IO(ExitCode.Success)
        case Left(e: Error) =>
          println(e.show)
          IO(ExitCode.Error)
        case Left(error) =>
          println(error.getMessage)
          IO(ExitCode.Error)
      }

  }
}
