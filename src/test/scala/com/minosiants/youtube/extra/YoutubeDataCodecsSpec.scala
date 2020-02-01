package com.minosiants
package youtube.extra

import cats.effect.IO
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.parser._
import org.specs2.execute.Result

import scala.util.Try

class YoutubeDataCodecsSpec extends YoutubeDataSpec {
  import YoutubeDataCodecsSpec._

  "YoutubeDataCodecsSpec" should {

    "YoutubeDataPlaylistItems be decoded properly" in {
      checkDecoding[YoutubeDataPlaylistItems]("__files/playlist.json")
        .unsafeRunSync()
    }

    "YoutubeDataVideos be decoded properly" in {
      checkDecoding[YoutubeDataVideos]("__files/videos.json").unsafeRunSync()
    }

    "bla" in {
      val r = IO.fromTry(Try[String](throw new RuntimeException("exeption >>>>> ")))
        .bracket(a => IO("Hello")){ a =>
          println(">>>> final")
          IO("bls")
        }
        .attempt
      println(r.unsafeRunSync())
      println(
        (try{
        throw new RuntimeException("exeption >>>>> ")
        IO("Hello")
      }catch {
          case e: Throwable => IO.raiseError(e)
      }).attempt.unsafeRunSync()
      )
      success
    }

  }

  def checkDecoding[A: Decoder](filename: String): IO[Result] = {
    (for {
      json <- loadFile(filename)
      result = decode[A](json)
    } yield result) map toSpecResult
  }
}

object YoutubeDataCodecsSpec {

  def loadFile(name: String): IO[String] = {
    IO {
      val f = getClass().getClassLoader().getResource(name).toURI
      scala.io.Source.fromFile(f)
    }.bracket { s =>
      IO(s.mkString)
    } { s =>
      IO(s.close())
    }
  }

}
