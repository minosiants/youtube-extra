package com.minosiants
package youtube.extra

import java.io.{File, PrintWriter}

import cats.effect.IO
import io.circe.syntax._
import io.circe.generic.auto._
import scala.util.Try

case class PlaylistGenerator() {

  def createPlaylist(videos: YoutubeDataVideos, destination: File) = {
    val escapedVideos = YoutubeDataVideos.titleAndDescriptionLens.modify(xml.Utility.escape)(videos)
    val json = escapedVideos.asJson.toString()
    for {
      template <- loadFile("templates/playlist.html")
      result = template.replace("@playlist@", json)
      _ <- saveToFile(result, destination)
    } yield ()
  }

  def saveToFile(text:String, destination:File):IO[Unit] ={
    IO.fromTry(Try[Unit]{
      val p = new PrintWriter(destination)
      p.write(text)
      p.close()
    })

  }
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