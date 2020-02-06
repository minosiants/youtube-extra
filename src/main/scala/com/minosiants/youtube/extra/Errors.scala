package com.minosiants
package youtube.extra

import cats.Show

import scala.util.control.ControlThrowable

sealed abstract class Error extends ControlThrowable

final case class CommandNotFound(message: String)     extends Error
final case class NoRequiredArguments(message: String) extends Error
final case class PlaylistNotFound(message: String)    extends Error

object Error {

  implicit lazy val ErrorShow: Show[Error] = Show.show {
    case CommandNotFound(message)     => s"Command not found: $message"
    case NoRequiredArguments(message) => s"No required arguments: $message"
    case PlaylistNotFound(message)    => message
  }
}
