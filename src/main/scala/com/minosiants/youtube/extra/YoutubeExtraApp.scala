package com.minosiants
package youtube.extra

import java.io.File

import cats.effect.{ ContextShift, ExitCode, IO }
import cats.instances.string._
import cats.syntax.functor._

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
  ): IO[Unit] = withClient(token) { client =>
    for {
      playlist <- client.getFullPlaylist(playlistId)
      _        <- createPlaylist(playlist, destination)
    } yield ()
  }

  def subscriptions(
      channelId: String,
      token: String,
      destination: File
  ): IO[Unit] = withClient(token) { client =>
    for {
      subs <- client.getSubsActivity(channelId)
      _    <- SubscriptionsGenerator.createSubscriptions(subs, destination)
    } yield ()

  }

  def help(): IO[Unit] = {
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
        |subscriptions -id -t (-d)
        |
        |   -id - channel id
        |   -t  - OAuth token (can be obtained here: https://tinyurl.com/vltl9p6)
        |   -d  - optional directory where subscriptions will be saved. Default value is "."
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
        case PlaylistCommand(playlistId, token, destination) =>
          playlist(playlistId, token, destination)
        case SubscriptionsCommand(channelId, token, destination) =>
          subscriptions(channelId, token, destination)
      }
      .attempt
      .flatMap {
        case Right(_) => IO(ExitCode.Success)
        case Left(e: Error) =>
          c.putStrLn(e).as(ExitCode.Error)
        case Left(error) =>
          c.putStrLn(error.getMessage).as(ExitCode.Error)
      }
}
