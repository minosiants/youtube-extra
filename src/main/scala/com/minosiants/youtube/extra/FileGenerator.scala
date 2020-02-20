package com.minosiants
package youtube.extra

import java.io.File

import cats.effect.IO
import io.circe.syntax._
import io.circe.generic.auto._
import FileUtils._
import StringUtils._
import com.minosiants.youtube.extra.data.{ FullPlaylist, Subscriptions }

object FileGenerator {

  def playlist(
      playlist: FullPlaylist,
      destination: File
  ): IO[Unit] = {

    val json         = escape(playlist).asJson.noSpaces
    val filename     = escapeFilename(playlist.playlistInfo.snippet.title)
    val playlistFile = destination / s"$filename.html"

    create(json, "playlist", playlistFile)
  }

  def subscriptions(
      subscriptions: Subscriptions,
      destination: File
  ): IO[Unit] = {

    val json     = escape(subscriptions).asJson.noSpaces
    val filename = escapeFilename(subscriptions.owner.snippet.title)
    val file     = destination / s"$filename-subscriptions.html"

    create(json, "subscriptions", file)
  }

  private def create(json: String, name: String, file: File): IO[Unit] =
    for {
      template <- loadFile(s"/templates/$name.html")
      result = template.replace(s"@$name@", json)
      _ <- mkParentDirs(file)
      _ <- saveToFile(result, file)
    } yield ()

  private def escape(subscriptions: Subscriptions): Subscriptions = {
    Function.chain(
      Seq(
        Subscriptions.ownerTextFieldsLens.modify(escapeHtml),
        Subscriptions.subTextFieldsLens.modify(escapeHtml),
        Subscriptions.videoTextFieldsLens.modify(escapeHtml)
      )
    )(subscriptions)
  }

  private def escape(playlist: FullPlaylist): FullPlaylist = {
    Function.chain(
      Seq(
        FullPlaylist.playlistTextFieldsLens.modify(escapeHtml),
        FullPlaylist.videoTextFiledsLens.modify(escapeHtml)
      )
    )(playlist)
  }

  private def escapeFilename(str: String): String = {
    str.toLowerCase.replaceAll("\\s+", "-")
  }
}
