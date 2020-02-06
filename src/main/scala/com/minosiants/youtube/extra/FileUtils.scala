package com.minosiants
package youtube.extra

import java.io.{ File, PrintWriter }

import cats.effect.IO
import scala.util.Try

object FileUtils {

  def saveToFile(text: String, destination: File): IO[Unit] =
    IO.fromTry(Try[Unit] {
      val p = new PrintWriter(destination)
      p.write(text)
      p.close()
    })

  def loadFile(name: String): IO[String] =
    IO {
      val f = getClass().getClassLoader().getResource(name).toURI
      scala.io.Source.fromFile(f)
    }.bracket { s =>
      IO(s.mkString)
    } { s =>
      IO(s.close())
    }

  def mkParentDirs(file: File): IO[Unit] =
    IO.fromTry(Try[Unit] {
      val parent = file.getParentFile()
      if (!parent.exists() && !parent.mkdirs()) {
        throw new IllegalStateException("Couldn't create dir: " + parent);
      }
    })
}
