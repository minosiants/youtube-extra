package com.minosiants
package youtube.extra

import java.io.File

sealed trait Command

final case class PlaylistCommand(
    playlistId: String,
    token: String,
    destination: File
) extends Command

object PlaylistCommand {
  def apply(playlistId: String, token: String, destination: Option[File]) =
    new PlaylistCommand(playlistId, token, destination.getOrElse(new File(".")))
}

final case class SubscriptionsCommand(
    channelId: String,
    token: String,
    destination: File
) extends Command

object SubscriptionsCommand {
  def apply(channelId: String, token: String, destination: Option[File]) =
    new SubscriptionsCommand(
      channelId,
      token,
      destination.getOrElse(new File("."))
    )
}

case object HelpCommand extends Command
