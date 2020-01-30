package com.minosiants
package youtube.extra

import org.specs2.mutable._
import io.circe.generic.auto._
import io.circe.parser._

class YoutubeDataPlaylistItemsSpec extends Specification {

  "YoutubeDataPlaylistItems" should {
    "be decoded properly" in {

      val f        = getClass().getClassLoader().getResource("__files/playlist.json").toURI
      val playlist = scala.io.Source.fromFile(f).mkString

      val result = decode[YoutubeDataPlaylistItems](playlist)
      result.isRight mustEqual true
    }

  }
}
