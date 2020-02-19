package com.minosiants
package youtube.extra

import cats.effect.IO
import io.circe.Decoder
import io.circe.generic.auto._
import org.specs2.execute.Result

import data.youtube._
import data._

class YoutubeDataCodecsSpec extends YoutubeDataSpec {

  "YoutubeDataCodecsSpec" should {

    "YoutubeDataPlaylistItems be decoded properly" in {
      checkDecoding[YoutubeDataPage[YoutubeDataItem]]("/__files/playlist.json")
        .unsafeRunSync()
    }

    "YoutubeDataVideo be decoded properly" in {
      checkDecoding[YoutubeDataPage[YoutubeDataVideo]]("/__files/videos2.json")
        .unsafeRunSync()
    }

    "YoutubeDataPlaylist be decoded properly" in {
      checkDecoding[YoutubeDataPage[YoutubeDataPlaylist]](
        "/__files/playlists.json"
      ).unsafeRunSync()
    }

    "YoutubeDataSubscription be decoded properly" in {
      checkDecoding[YoutubeDataPage[YoutubeDataSubscription]](
        "/__files/subscriptions.json"
      ).unsafeRunSync()
    }

    "YoutubeDataChannel be decoded properly" in {
      checkDecoding[YoutubeDataPage[YoutubeDataChannel]](
        "/__files/channels.json"
      ).unsafeRunSync()
    }

  }

  def checkDecoding[A: Decoder](filename: String): IO[Result] =
    decodeJson(filename).attempt.map(toSpecResult)

}
