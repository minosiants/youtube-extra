package com.minosiants
package youtube.extra

import java.io.{ BufferedReader, PrintStream }

import cats.Show
import cats.effect.IO
import cats.syntax.show._

trait Console {
  def putStrLn[A: Show](a: A): IO[Unit]
  def putStr[A: Show](a: A): IO[Unit]
  def redLn: IO[String]
}

object Console {
  implicit val std: Console = StdConsole.console
}

case class StdConsole(in: BufferedReader, out: PrintStream) extends Console {
  override def putStrLn[A: Show](a: A): IO[Unit] = IO {
    out.println(a.show)
  }

  override def putStr[A: Show](a: A): IO[Unit] = IO {
    out.print(a.show)
  }

  override def redLn: IO[String] = IO {
    in.readLine()
  }
}
object StdConsole {
  def console: Console = new StdConsole(scala.Console.in, scala.Console.out)
}
