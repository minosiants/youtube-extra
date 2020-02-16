package com.minosiants
package youtube.extra

//import org.specs2.matcher._
//import org.specs2.specification._
import cats.effect.specs2.CatsIO
import cats.effect.{ ContextShift, IO }
import org.specs2.mutable._
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.http4s.Uri
import org.http4s.client.blaze.BlazeClientBuilder
import org.specs2.execute.Result

import scala.concurrent.ExecutionContext.global

class YoutubeDataClientSpec extends YoutubeDataSpec with After with CatsIO {

  val wireMockServer: WireMockServer = new WireMockServer(
    wireMockConfig().port(0)
  )
  wireMockServer.start()

  val baseUri = Uri.unsafeFromString(s"http://localhost:${wireMockServer.port}")

  val key = "AIzaSyAa8yy0GdcGPHdtD083HiGGx_S0vMPScDM"
  val token =
    "ya29.ImS7BzsX8ziHDuAX8Y7O4lmZ55_sBiNVXXPakKmnDwfUFJ_kvdZ4LZfLTTnfMAIXKIGRBbBsCHaHMU33GXtX0lzjElI6wz7TFNDn8G0eNXxmoqamGojmU03fJMCQ2cmb52fXWQMq"
  val playlistId = "PLBCF2DAC6FFB574DE"
  val props      = YoutubeDataAccessProps(key, token)

  "YoutubeDataClint" should {

    "get playlist items" in {
      withClient(_.getPlaylistItems(playlistId))
        .map(toSpecResult)
        .unsafeRunSync()
    }

    "get playlist items wit pagination" in {
      withClient(_.getPlaylistItems("PLLMLOC3WM2r5KDwkSRrLJ1_O6kZqlhhFt"))
        .map {
          case Right(playlist) => playlist.size mustEqual 10
          case Left(error) =>
            failure(error.getMessage)
            10 mustEqual 2
        }
        .unsafeRunSync()

    }

    "get videos" in {
      val ids = List("GvgqDSnpRQM", "V4DDt30Aat4", "XDgC4FMftpg")
      withClient(_.getVideos(ids)).map(toSpecResult).unsafeRunSync()

    }

    "get playlist videos" in {
      withClient(_.getFullPlaylist(playlistId))
        .map(toSpecResult)
        .unsafeRunSync()
    }

    "get playlists" in {
      withClient(_.getPlaylists(playlistId)).map(toSpecResult).unsafeRunSync()
    }
  }

  def withClient[A](
      youtubeClient: YoutubeDataClient => IO[A]
  ) = {
    BlazeClientBuilder[IO](global).resource.use { client =>
      youtubeClient(YoutubeDataClient(client, baseUri, props))
    }.attempt
  }

  override def after: Any = {

    // wireMockServer.shutdown()

  }
}
