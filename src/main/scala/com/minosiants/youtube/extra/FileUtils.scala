package com.minosiants
package youtube.extra

import java.io.{ File, InputStream, PrintWriter }
import java.net.URL

import cats.effect.IO

import scala.util.Try

object FileUtils {

  def saveToFile(text: String, destination: File): IO[Unit] =
    IO.fromTry(Try[Unit] {
      val p = new PrintWriter(destination)
      p.write(text)
      p.close()
    })

  def loadFile(file: String): IO[String] =
    IO.fromTry(Try {
      val is     = getClass().getResourceAsStream(file)
      val result = scala.io.Source.fromInputStream(is).mkString
      is.close()
      result
    })

  def mkParentDirs(file: File): IO[Unit] =
    IO.fromTry(Try[Unit] {
      val parent = file.getParentFile()
      if (!parent.exists() && !parent.mkdirs()) {
        throw new IllegalStateException("Couldn't create dir: " + parent);
      }
    })
}
