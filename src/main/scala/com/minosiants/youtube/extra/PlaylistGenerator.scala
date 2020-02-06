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

    val json = escape(playlist).asJson.noSpaces
    val filename = titleToFilename(playlist)
    val playlistFile = destination / s"$filename.html"

    for {
      template <- loadFile("/templates/playlist.html")
      result = template.replace("@playlist@", json)
      _ <- mkParentDirs(playlistFile)
      _ <- saveToFile(result, playlistFile)
    } yield ()
  }

  private def escape(playlist:FullPlaylist):FullPlaylist = {
    val pl =
      FullPlaylist.playlistTitleAndDescriptionLens.modify(escapeHtml)(playlist)
      FullPlaylist.videoTitleAndDescriptionLens.modify(escapeHtml)(pl)
  }
  private def titleToFilename(playlist:FullPlaylist):String = {
    playlist.playlistInfo.snippet.title.toLowerCase.replaceAll("\\s+", "-")
  }
}
