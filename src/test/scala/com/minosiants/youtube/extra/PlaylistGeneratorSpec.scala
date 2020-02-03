package com.minosiants.youtube.extra

import java.io.File

import cats.effect.specs2.CatsIO
import io.circe.generic.auto._

class PlaylistGeneratorSpec extends YoutubeDataSpec with CatsIO {

  "PlaylistGenerator" should {

    "create playlist" in {
      val destination = new File("target/playlist/test.html")
      (for {
        videos <- decodeJson[YoutubeDataVideos]("__files/videos.json")
        _      <- PlaylistGenerator.createPlaylist(videos, destination)
      } yield ()).attempt.map(toSpecResult).unsafeRunSync()

    }

  }
}
