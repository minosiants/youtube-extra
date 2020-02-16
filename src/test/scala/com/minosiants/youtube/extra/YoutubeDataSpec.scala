package com.minosiants
package youtube.extra

import cats.effect.IO
import io.circe.Decoder
import io.circe.parser.decode
import org.specs2.execute.Result
import org.specs2.mutable._

abstract class YoutubeDataSpec extends Specification {

  def toSpecResult[A](result: Either[Throwable, A]): Result = result match {
    case Right(_) => success
    case Left(value) =>
      failure(value.getMessage)
  }

  def decodeJson[A: Decoder](filename: String): IO[A] = {
    for {
      json   <- FileUtils.loadFile(filename)
      result <- IO.fromEither(decode[A](json))
    } yield result
  }
}
