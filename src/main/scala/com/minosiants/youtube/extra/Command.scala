package com.minosiants
package youtube.extra

import java.io.File

trait Command

case class PlaylistCommand(
    playlistId: String,
    token: String,
    destination: Option[File]
) extends Command

case object HelpCommand extends Command
