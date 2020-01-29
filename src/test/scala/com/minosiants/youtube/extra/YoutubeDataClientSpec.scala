package com.minosiants
package youtube.extra

//import org.specs2.matcher._
//import org.specs2.specification._
import cats.effect.specs2.CatsIO
import cats.effect.{ContextShift, IO}
import org.specs2.mutable._
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.http4s.Uri
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext.global

class YoutubeDataClientSpec extends Specification with After with CatsIO {

    val wireMockServer: WireMockServer = new WireMockServer(wireMockConfig().port(0))
    wireMockServer.start()

    val baseUri = Uri.unsafeFromString(s"http://localhost:${wireMockServer.port}")

    val key = "AIzaSyAa8yy0GdcGPHdtD083HiGGx_S0vMPScDM"
    val token = "ya29.ImS7BzsX8ziHDuAX8Y7O4lmZ55_sBiNVXXPakKmnDwfUFJ_kvdZ4LZfLTTnfMAIXKIGRBbBsCHaHMU33GXtX0lzjElI6wz7TFNDn8G0eNXxmoqamGojmU03fJMCQ2cmb52fXWQMq"
    val playlistId = "PLBCF2DAC6FFB574DE"

  //implicit val cs: ContextShift[IO] = IO.contextShift(global)

  "YoutubeDataClint" should {

    "get play list" in {
      println(">>>>>>>>>>playlist1 ")
      BlazeClientBuilder[IO](global).resource
        .use { client =>
          val youtubeClient = YoutubeDataClient(client, baseUri, YoutubeDataAccessProps(key, token))



          youtubeClient.getPlayList(playlistId).map{v=> println(">>>>>>>>>>playlist"); v }



        }
      1 + 1 mustEqual 2
    }

  }

    override def after: Any = {
      wireMockServer.shutdown()
    }
}