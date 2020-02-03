package com.minosiants.youtube.extra

import java.io.File

import cats.Applicative
import cats.effect.IO
import cats.implicits._

import scala.annotation.tailrec

object CommandLineParser {

  def parseCommand(args: List[String]): IO[Command] = {
    args match {
      case "playlist" :: tail => playlistCommand(tail)
      case _                  => IO.raiseError(CommandNotFound("No valid command found."))
    }
  }

  def playlistCommand(args: List[String]): IO[Command] = {

    @tailrec
    def go(
        l: List[String],
        result: Map[String, String]
    ): Map[String, String] = {
      l match {
        case "-id" :: value :: xs => go(xs, Map("id" -> value) ++ result)
        case "-t" :: value :: xs  => go(xs, Map("t" -> value) ++ result)
        case "-d" :: value :: xs  => go(xs, Map("d" -> value) ++ result)
        case _                    => result

      }
    }

    val result = go(args, Map.empty[String, String])
    createPlaylistCommand(result)
  }

  def createPlaylistCommand(args: Map[String, String]): IO[Command] =
    Applicative[Option]
      .map2(args.get("id"), args.get("t"))(
        (id, token) =>
          PlaylistCommand(id, token, args.get("d").map(v => new File(v)))
      )
      .fold(
        IO.raiseError[PlaylistCommand](
          NoRequiredArguments("'id' and 't' should be provided for playlist")
        )
      )(IO.pure)

}
