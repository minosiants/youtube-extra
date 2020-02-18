package com.minosiants
package youtube.extra

import cats.Show
import cats.effect.IO

import scala.util.control.ControlThrowable

sealed abstract class Error extends ControlThrowable

final case class CommandNotFound(message: String)     extends Error
final case class NoRequiredArguments(message: String) extends Error
final case class PlaylistNotFound(message: String)    extends Error
final case class ChannelNotFound(message: String)     extends Error
final case class ResourceNotFound(message: String)    extends Error
final case class UnableCreateDir(message: String)     extends Error
final case class UnableWriteFile(message: String)     extends Error
final case class UnableCloseResource(message: String) extends Error

object Error {

  implicit lazy val ErrorShow: Show[Error] = Show.show {
    case CommandNotFound(message)     => s"Command not found: $message"
    case NoRequiredArguments(message) => s"No required arguments: $message"
    case PlaylistNotFound(message)    => s"Playlist not found $message"
    case ChannelNotFound(message)     => s"Channel not found $message"
    case ResourceNotFound(message)    => s"Resource not found: $message"
    case UnableCreateDir(message)     => s"Unable to create directory: $message"
    case UnableWriteFile(message)     => s"Unable write to file: $message"
    case UnableCloseResource(message) => s"Unable close resource: $message"
  }

  def unableWriteFile[A](message: String): IO[A] =
    IO.raiseError(UnableWriteFile(message))

  def unableCloseResource[A](message: String): IO[A] =
    IO.raiseError(UnableCloseResource(message))

  def unableCreateDir[A](message: String): IO[A] =
    IO.raiseError(UnableCreateDir(message))

  def resourceNotFound[A](message: String): IO[A] =
    IO.raiseError(ResourceNotFound(message))

  def channelNotFound[A](message: String): IO[A] =
    IO.raiseError(ChannelNotFound(message))

}
