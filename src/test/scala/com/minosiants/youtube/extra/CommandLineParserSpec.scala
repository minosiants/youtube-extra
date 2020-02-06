package com.minosiants
package youtube.extra
import java.io.File

class CommandLineParserSpec extends YoutubeDataSpec {

  "CommandLineParser" should {

    val args = List("playlist", "-id", "id-value", "-t", "token-value")

    "parse playlist command without destination" in {
      val result = CommandLineParser.parseArgs(args).attempt.unsafeRunSync()
      result match {
        case Right(PlaylistCommand("id-value", "token-value", None)) => success
        case _                                                       => failure
      }
    }

    "parse playlist command with destination" in {
      val result = CommandLineParser
        .parseArgs(args ++ List("-d", "destination-value"))
        .attempt
        .unsafeRunSync()
      val e = PlaylistCommand(
        "id-value",
        "token-value",
        Some(new File("destination-value"))
      )
      result mustEqual Right(e)
    }

    "parse playlist command without playlist option" in {
      val result = CommandLineParser
        .parseArgs(List("-d", "destination-value"))
        .attempt
        .unsafeRunSync()
      result mustEqual Left(CommandNotFound("No valid command found."))
    }

    "parse playlist command without token option" in {
      val result = CommandLineParser
        .parseArgs(
          List("playlist", "-id", "id-value", "-d", "destination-value")
        )
        .attempt
        .unsafeRunSync()
      result mustEqual Left(
        NoRequiredArguments("'-id' and '-t' should be provided for playlist")
      )
    }
  }
}