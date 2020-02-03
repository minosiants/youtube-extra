package com.minosiants
package youtube.extra

import scala.util.control.ControlThrowable

sealed abstract class Error extends ControlThrowable

final case class CommandNotFound(message: String)     extends Error
final case class NoRequiredArguments(message: String) extends Error
