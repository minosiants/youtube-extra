package com.minosiants.youtube.extra

import java.io.File

import cats.effect.specs2.CatsIO
import io.circe.generic.auto._

class PlaylistGeneratorSpec extends YoutubeDataSpec with CatsIO {

  "PlaylistGenerator" should {
    val playlistGen = PlaylistGenerator()
    "create playlist" in {
      val destination = new File("target/playlist/test.html")
      (for {
        videos <- decodeJson[YoutubeDataVideos]("__files/videos.json")
        _      <- playlistGen.createPlaylist(videos, destination)
      } yield ()).attempt.map(toSpecResult).unsafeRunSync()

    }
    "escape" in {
      val st = "hit northeast Australia, Mark's"
      println(xml.Utility.escape(st))
      success
    }
  }
}
