package com.minosiants
package youtube.extra

import cats.effect.IO
import io.circe.Decoder
import io.circe.generic.auto._
import org.specs2.execute.Result

class YoutubeDataCodecsSpec extends YoutubeDataSpec {

  "YoutubeDataCodecsSpec" should {

    "YoutubeDataPlaylistItems be decoded properly" in {
      checkDecoding[YoutubeDataPlaylistItems]("__files/playlist.json")
        .unsafeRunSync()
    }

    "YoutubeDataVideos be decoded properly" in {
      checkDecoding[YoutubeDataVideos]("__files/videos2.json").unsafeRunSync()
    }
    "YoutubeDataPlaylists be decoded properly" in {
      checkDecoding[YoutubeDataPlaylists]("__files/playlists.json")
        .unsafeRunSync()
    }

  }

  def checkDecoding[A: Decoder](filename: String): IO[Result] =
    decodeJson(filename).attempt.map(toSpecResult)

}
