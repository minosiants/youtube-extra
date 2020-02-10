package com.minosiants
package youtube.extra

import cats.effect.{ ContextShift, IO, _ }

object Main extends IOApp {

  import Console.std
  implicit val global               = scala.concurrent.ExecutionContext.global
  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  override def run(args: List[String]): IO[ExitCode] =
    YoutubeExtraApp().runApp(args)

}
