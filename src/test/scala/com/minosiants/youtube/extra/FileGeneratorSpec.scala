package com.minosiants.youtube.extra

import java.io.File

import cats.effect.specs2.CatsIO
import io.circe.generic.auto._

class FileGeneratorSpec extends YoutubeDataSpec with CatsIO {

  "File Generator" should {

    "create playlist" in {
      val destination = new File("target/playlist")
      (for {
        playlist <- decodeJson[YoutubeDataPage[YoutubeDataPlaylist]](
          "/__files/playlists.json"
        ).map(_.items.head)
        videos <- decodeJson[YoutubeDataPage[YoutubeDataVideo]](
          "/__files/videos.json"
        )
        _ <- FileGenerator.playlist(
          FullPlaylist(playlist, videos.items),
          destination
        )
      } yield ()).attempt.map(toSpecResult).unsafeRunSync()

    }

  }
}
