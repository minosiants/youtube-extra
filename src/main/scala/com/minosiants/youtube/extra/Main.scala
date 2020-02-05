package com.minosiants
package youtube.extra

import java.io.File

import cats.effect._
import cats.implicits._
import com.minosiants.youtube.extra.PlaylistGenerator.createPlaylist
import com.minosiants.youtube.extra.YoutubeDataAccessProps.props
import com.minosiants.youtube.extra.YoutubeDataClient.{ apiUri, googleAppKey }
import org.http4s.client.blaze._
//import org.http4s.Uri._
import cats.effect.{ ContextShift, IO }

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {

  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  /* val baseUri = YoutubeDataClient.apiUri
  val key     = ""
  val token   = ""*/

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
          playlist <- ydClient.getPlaylistVideos(playlistId)
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
