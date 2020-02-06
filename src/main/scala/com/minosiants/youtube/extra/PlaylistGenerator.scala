package com.minosiants
package youtube.extra

import java.io.File

import cats.effect.IO
import io.circe.syntax._
import io.circe.generic.auto._
import FileUtils._
import StringUtils._

object PlaylistGenerator {

  def createPlaylist(
      playlist: FullPlaylist,
      destination: File
  ): IO[Unit] = {

    val pl =
      FullPlaylist.playlistTitleAndDescriptionLens.modify(escapeHtml)(playlist)
    val escapedPlaylist =
      FullPlaylist.videoTitleAndDescriptionLens.modify(escapeHtml)(pl)

    val json = escapedPlaylist.asJson.noSpaces
    val title =
      playlist.playlistInfo.snippet.title.toLowerCase.replaceAll("\\s+", "-")
    val file = destination / s"$title.html"
    for {
      template <- loadFile("templates/playlist.html")
      result = template.replace("@playlist@", json)
      _ <- mkParentDirs(file)
      _ <- saveToFile(result, file)
    } yield ()
  }

}
