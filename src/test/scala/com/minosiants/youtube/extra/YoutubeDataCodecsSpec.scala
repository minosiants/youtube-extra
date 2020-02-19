package com.minosiants
package youtube.extra

import cats.effect.IO
import io.circe.Decoder
import io.circe.generic.auto._
import org.specs2.execute.Result

import data.youtube.{ Subscription => YTSub, _ }
import data._

class YoutubeDataCodecsSpec extends YoutubeDataSpec {

  "YoutubeDataCodecsSpec" should {

    "YoutubeDataPlaylistItems be decoded properly" in {
      checkDecoding[Page[Item]]("/__files/playlist.json")
        .unsafeRunSync()
    }

    "YoutubeDataVideo be decoded properly" in {
      checkDecoding[Page[Video]]("/__files/videos2.json")
        .unsafeRunSync()
    }

    "YoutubeDataPlaylist be decoded properly" in {
      checkDecoding[Page[Playlist]](
        "/__files/playlists.json"
      ).unsafeRunSync()
    }

    "YoutubeDataSubscription be decoded properly" in {
      checkDecoding[Page[YTSub]](
        "/__files/subscriptions.json"
      ).unsafeRunSync()
    }

    "YoutubeDataChannel be decoded properly" in {
      checkDecoding[Page[Channel]](
        "/__files/channels.json"
      ).unsafeRunSync()
    }

  }

  def checkDecoding[A: Decoder](filename: String): IO[Result] =
    decodeJson(filename).attempt.map(toSpecResult)

}
