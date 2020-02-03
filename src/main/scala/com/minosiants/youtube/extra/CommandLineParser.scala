package com.minosiants.youtube.extra

import java.io.File

import cats.Applicative
import cats.effect.IO
import cats.implicits._

object CommandLineParser {

  def parseCommand(args: List[String]): IO[Command] = {
    args match {
      case "playlist" :: tail => playlistCommand(tail)
      case _                  => IO.raiseError(CommandNotFound("No valid command found."))
    }
  }

  def playlistCommand(args: List[String]): IO[Command] = {

    def go(l: List[String]): Map[String, String] = {
      l match {
        case "-id" :: value :: xs => Map("id" -> value) ++ go(xs)
        case "-t" :: value :: xs  => Map("t" -> value) ++ go(xs)
        case "-d" :: value :: xs  => Map("d" -> value) ++ go(xs)
        case Nil                  => Map.empty[String, String]
      }
    }
    val result = go(args)

    Applicative[Option]
      .map2(result.get("id"), result.get("t"))(
        (id, token) =>
          PlaylistCommand(id, token, result.get("d").map(v => new File(v)))
      )
      .fold(
        IO.raiseError[PlaylistCommand](
          NoRequiredArguments("'id' and 't' should be provided for playlist")
        )
      )(IO.pure)
  }
}
