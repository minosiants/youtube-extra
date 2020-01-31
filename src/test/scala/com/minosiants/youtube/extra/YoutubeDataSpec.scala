package com.minosiants
package youtube.extra

import org.specs2.execute.Result
import org.specs2.mutable._

abstract class YoutubeDataSpec extends Specification {

  def toSpecResult[A](result: Either[Throwable, A]): Result = result match {
    case Right(_)    => success
    case Left(value) => failure(value.getMessage)
  }

}
