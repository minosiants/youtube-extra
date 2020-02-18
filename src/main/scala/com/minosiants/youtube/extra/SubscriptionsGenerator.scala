package com.minosiants
package youtube.extra

import java.io.File

import cats.effect.IO
import io.circe.syntax._
import io.circe.generic.auto._
import FileUtils._
import StringUtils._

object SubscriptionsGenerator {
  def createSubscriptions(
      activity: SubscriptionsActivity,
      destination: File
  ): IO[Unit] = {
    ???
  }
}
