package com.minosiants.youtube.extra

import java.io.File

import cats.effect.specs2.CatsIO
import com.minosiants.youtube.extra.PlaylistGenerator.createPlaylist
import io.circe.generic.auto._

class PlaylistGeneratorSpec extends YoutubeDataSpec with CatsIO {

  "PlaylistGenerator" should {

    "create playlist" in {
      val destination = new File("target/playlist")
      (for {
        playlist <- decodeJson[GoogleDataPage[YoutubeDataPlaylist]](
          "__files/playlists.json"
        ).map(_.items.head)
        videos <- decodeJson[GoogleDataPage[YoutubeDataVideo]](
          "__files/videos.json"
        )
        _ <- createPlaylist(
          FullPlaylist(playlist, videos.items),
          destination
        )
      } yield ()).attempt.map(toSpecResult).unsafeRunSync()

    }

  }
}
