package com.minosiants
package youtube.extra

import java.io.{ File, InputStream, PrintWriter }

import cats.effect.{ IO, Resource }

object FileUtils {

  def saveToFile(text: String, destination: File): IO[Unit] =
    printWriter(destination)
      .use(pw => IO(pw.write(text)))
      .handleErrorWith(_ => Error.unableWriteFile(destination.getAbsolutePath))

  def printWriter(file: File): Resource[IO, PrintWriter] =
    Resource.make {
      IO(new PrintWriter(file))
    } { pw =>
      IO(pw.close())
        .handleErrorWith(_ => Error.unableCloseResource(file.getAbsolutePath))
    }

  def inputStream(file: String): Resource[IO, InputStream] =
    Resource.make {
      IO(getClass().getResourceAsStream(file))
    } { is =>
      IO(is.close()).handleErrorWith(_ => Error.unableCloseResource(file))
    }

  def loadFile(file: String): IO[String] = inputStream(file).use { is =>
    IO {
      scala.io.Source.fromInputStream(is)
    }.bracket(bs => IO(bs.mkString)) { bss =>
      bss.close()
      IO.unit
    }
  }

  def mkParentDirs(file: File): IO[Unit] =
    IO(file.getParentFile()).bracket { parent =>
      if (!parent.exists() && !parent.mkdirs())
        Error.unableCreateDir(parent.getAbsolutePath)
      else
        IO.unit
    }(_ => IO.unit)

}
