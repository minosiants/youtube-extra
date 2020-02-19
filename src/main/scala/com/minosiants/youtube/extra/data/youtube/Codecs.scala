package com.minosiants.youtube.extra
package data.youtube

import java.time.Instant
import cats.syntax.either._

import io.circe.{ Decoder, Encoder }

trait Codecs {
  implicit val encodeInstant: Encoder[Instant] =
    Encoder.encodeString.contramap[Instant](_.toString)
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emap {
    str =>
      Either.catchNonFatal(Instant.parse(str)).leftMap(_.toString)
  }
}
