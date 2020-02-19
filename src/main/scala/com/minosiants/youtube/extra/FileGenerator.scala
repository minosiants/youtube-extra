package com.minosiants
package youtube.extra

import java.io.File

import cats.effect.IO
import io.circe.syntax._
import io.circe.generic.auto._
import FileUtils._
import StringUtils._

object FileGenerator {

  def playlist(
      playlist: FullPlaylist,
      destination: File
  ): IO[Unit] = {

    val json         = escape(playlist).asJson.noSpaces
    val filename     = escapeFliename(playlist.playlistInfo.snippet.title)
    val playlistFile = destination / s"$filename.html"

    create(json, "playlist", playlistFile)
  }

  def subscriptions(
      subscriptions: Subscriptions,
      destination: File
  ): IO[Unit] = {

    val json     = escape(subscriptions).asJson.noSpaces
    val filename = escapeFliename(subscriptions.owner.snippet.title)
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
        Subscriptions.ownerTitleAndDescriptionLens.modify(escapeHtml),
        Subscriptions.subTitleAndDescriptionLens.modify(escapeHtml),
        Subscriptions.videoTitleAndDescriptionLens.modify(escapeHtml)
      )
    )(subscriptions)
  }

  private def escape(playlist: FullPlaylist): FullPlaylist = {
    Function.chain(
      Seq(
        FullPlaylist.playlistTitleAndDescriptionLens.modify(escapeHtml),
        FullPlaylist.videoTitleAndDescriptionLens.modify(escapeHtml)
      )
    )(playlist)
  }

  private def escapeFliename(str: String): String = {
    str.toLowerCase.replaceAll("\\s+", "-")
  }
}
