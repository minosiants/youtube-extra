package com.minosiants
package youtube.extra

import java.io.File

import cats.effect._
import org.http4s.client.blaze._
import cats.effect.{ ContextShift, IO }
import scala.concurrent.ExecutionContext.global

import PlaylistGenerator.createPlaylist
import YoutubeDataAccessProps.props
import YoutubeDataClient.{ apiUri, googleAppKey }

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

  override def run(args: List[String]): IO[ExitCode] = {

    CommandLineParser
      .parseArgs(args)
      .flatMap {
        case HelpCommand => IO { println("Program help message") }
        case PlaylistCommand(playlistId, token, Some(destination)) =>
          playlist(playlistId, token, destination)
        case PlaylistCommand(playlistId, token, None) =>
          playlist(playlistId, token, new File(s"./$playlistId.html"))
      }
      .attempt
      .flatMap {
        case Right(_) => IO(ExitCode.Success)
        case Left(error) =>
          println(error.getMessage)
          IO(ExitCode.Error)
      }

  }
}
