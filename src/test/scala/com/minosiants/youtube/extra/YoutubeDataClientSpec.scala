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
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext.global

class YoutubeDataClientSpec extends Specification with After with CatsIO {

  val wireMockServer: WireMockServer = new WireMockServer(wireMockConfig().port(0))
  wireMockServer.start()

  val baseUri = Uri.unsafeFromString(s"http://localhost:${wireMockServer.port}")

  val key = "AIzaSyAa8yy0GdcGPHdtD083HiGGx_S0vMPScDM"
  val token =
    "ya29.ImS7BzsX8ziHDuAX8Y7O4lmZ55_sBiNVXXPakKmnDwfUFJ_kvdZ4LZfLTTnfMAIXKIGRBbBsCHaHMU33GXtX0lzjElI6wz7TFNDn8G0eNXxmoqamGojmU03fJMCQ2cmb52fXWQMq"
  val playlistId    = "PLBCF2DAC6FFB574DE"
  val youtubeClient = (client: Client[IO]) => YoutubeDataClient(client, baseUri, YoutubeDataAccessProps(key, token))
  //implicit val cs: ContextShift[IO] = IO.contextShift(global)
  val props = YoutubeDataAccessProps(key, token)

  "YoutubeDataClint" should {

    "get play list" in {

      val result = BlazeClientBuilder[IO](global).resource
        .use(YoutubeDataClient(_, baseUri, props).getPlayList(playlistId))
        .unsafeRunSync()
      result.isRight mustEqual true

    }
    "get videos statistics" in {
      val ids = List("GvgqDSnpRQM", "V4DDt30Aat4", "07718Vcwcyc", "XDgC4FMftpg")
      val result = BlazeClientBuilder[IO](global).resource
        .use(YoutubeDataClient(_, baseUri, props).getVideosStatisitcs(ids))
        .unsafeRunSync()
      result.isRight mustEqual true
    }

  }

  override def after: Any = {

    // wireMockServer.shutdown()

  }
}
