package com.minosiants
package youtube.extra

import org.specs2.mutable._
import io.circe.generic.auto._
import io.circe.parser._

class YoutubeDataCodecsSpec extends Specification {
  import YoutubeDataCodecsSpec._

  "YoutubeDataPlaylistItems" should {
    "be decoded properly" in {

      val playlist = loadFile("__files/playlist.json")

      val result = decode[YoutubeDataPlaylistItems](playlist)
      result.isRight mustEqual true
    }

  }
  "YoutubeDataVideosStatistics" should {
    "be decoded properly" in {
      val videos = loadFile("__files/videos-statistics.json")
      val result = decode[YoutubeDataVideos](videos)
      println(result)
      result.isRight mustEqual true
    }
  }
}

object YoutubeDataCodecsSpec {

  def loadFile(name: String) = {
    val f = getClass().getClassLoader().getResource(name).toURI
    scala.io.Source.fromFile(f).mkString
  }
}
