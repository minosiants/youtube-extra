package com.minosiants
package youtube.extra

import java.io.{ File, PrintWriter }

import cats.effect.IO
import io.circe.syntax._
import io.circe.generic.auto._
import scala.util.Try

object PlaylistGenerator {

  def createPlaylist(
      videos: List[YoutubeDataVideo],
      destination: File
  ): IO[Unit] = {
    val escapedVideos = videos.map(
      YoutubeDataVideos.titleAndDescriptionLens.modify(
        escapeHtml
      )(_)
    )
    val json = escapedVideos.asJson.noSpaces
    for {
      template <- loadFile("templates/playlist.html")
      result = template.replace("@playlist@", json)
      _ <- createDestination(destination)
      _ <- saveToFile(result, destination)
    } yield ()
  }

  def escapeHtml(text: String): String = {
    xml.Utility
      .escape(text)
      .replaceAll("'", "&#39;")
      .replaceAll("(\r\n|\n)", "<br/>");
  }
  def createDestination(destination: File): IO[Unit] =
    IO.fromTry(Try[Unit] {
      val parent = destination.getParentFile()
      if (!parent.exists() && !parent.mkdirs()) {
        throw new IllegalStateException("Couldn't create dir: " + parent);
      }
    })

  def saveToFile(text: String, destination: File): IO[Unit] =
    IO.fromTry(Try[Unit] {
      val p = new PrintWriter(destination)
      p.write(text)
      p.close()
    })

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
