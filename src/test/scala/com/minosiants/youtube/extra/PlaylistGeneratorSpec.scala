package com.minosiants.youtube.extra

import java.io.File

import cats.effect.specs2.CatsIO
import com.minosiants.youtube.extra.PlaylistGenerator.createPlaylist
import io.circe.generic.auto._

class PlaylistGeneratorSpec extends YoutubeDataSpec with CatsIO {

  "PlaylistGenerator" should {

    "create playlist" in {
      val destination = new File("target/playlist/test.html")
      (for {
        playlist <- decodeJson[YoutubeDataPlaylists]("__files/playlists.json")
          .map(_.items.head)
        videos <- decodeJson[YoutubeDataVideos]("__files/videos.json")
        _ <- createPlaylist(
          FullPlaylist(playlist, videos.items),
          destination
        )
      } yield ()).attempt.map(toSpecResult).unsafeRunSync()

    }

  }
}
