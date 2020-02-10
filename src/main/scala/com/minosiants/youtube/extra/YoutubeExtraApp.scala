package com.minosiants
package youtube.extra

import java.io.File

import cats.effect.{ ContextShift, ExitCode, IO }
import cats.instances.string._
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext
import PlaylistGenerator.createPlaylist
import YoutubeDataAccessProps.props
import YoutubeDataClient.{ apiUri, googleAppKey }

case class YoutubeExtraApp()(
    implicit val cs: ContextShift[IO],
    implicit val ec: ExecutionContext,
    implicit val c: Console
) {

  private def withClient[A](
      token: String
  )(f: YoutubeDataClient => IO[A]): IO[A] =
    BlazeClientBuilder[IO](ec).resource.use(
      client => f(YoutubeDataClient(client, apiUri, props(googleAppKey, token)))
    )

  def playlist(
      playlistId: String,
      token: String,
      destination: File
  ): IO[Unit] = withClient[Unit](token) { client =>
    for {
      playlist <- client.getFullPlaylist(playlistId)
      _        <- createPlaylist(playlist, destination)
    } yield ()
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

    c.putStrLn(message)

  }

  def runApp(args: List[String]): IO[ExitCode] =
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
          c.putStrLn(e)
          IO(ExitCode.Error)
        case Left(error) =>
          c.putStrLn(error.getMessage)
          IO(ExitCode.Error)
      }
}
