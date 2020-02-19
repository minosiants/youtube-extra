package com.minosiants.youtube.extra

import java.io.File

import cats.Applicative
import cats.effect.IO
import cats.implicits._
import com.minosiants.youtube.extra.data.{
  Command,
  CommandNotFound,
  HelpCommand,
  NoRequiredArguments,
  PlaylistCommand,
  SubscriptionsCommand
}

import scala.annotation.tailrec

object CommandLineParser {

  def parseArgs(args: List[String]): IO[Command] = args match {
    case Nil                     => IO(HelpCommand)
    case "playlist" :: tail      => playlistCommand(tail)
    case "subscriptions" :: tail => subscriptionsCommand(tail)
    case _                       => IO.raiseError(CommandNotFound("No valid command found."))
  }

  private def parseOptions(args: List[String]): Map[String, String] = {
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

    go(args, Map.empty[String, String])
  }

  def playlistCommand(args: List[String]): IO[Command] = {
    val options = parseOptions(args)
    Applicative[Option]
      .map2(options.get("id"), options.get("t"))(
        (id, token) =>
          PlaylistCommand(id, token, options.get("d").map(new File(_)))
      )
      .fold(
        IO.raiseError[PlaylistCommand](
          NoRequiredArguments("'-id' and '-t' should be provided for playlist")
        )
      )(IO.pure)
  }

  def subscriptionsCommand(args: List[String]): IO[Command] = {
    val options = parseOptions(args)
    Applicative[Option]
      .map2(options.get("id"), options.get("t"))(
        (id, token) =>
          SubscriptionsCommand(
            id,
            token,
            options.get("d").map(new File(_))
          )
      )
      .fold(
        IO.raiseError[SubscriptionsCommand](
          NoRequiredArguments(
            "'-id' and '-t' should be provided for subscriptions"
          )
        )
      )(IO.pure)
  }

}
