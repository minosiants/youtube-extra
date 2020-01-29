package com.minosiants
package youtube.extra

import cats.effect.IO
import org.http4s.client.dsl.io._

import org.http4s._
import org.http4s.client.Client
import org.http4s.headers.{ Accept, Authorization }

final case class YoutubeDataAccessProps(key: String, token: String)

final case class YoutubeDataClient(client: Client[IO], baseUri:Uri, accessProps: YoutubeDataAccessProps) {

  private val apiUri = baseUri / "youtube" / "v3"

  private def playListItemsUri(playlistId: String): Uri =
    apiUri / "playlistItems" +? ("key", accessProps.key) +? ("playlistId", playlistId) +? ("part", "snippet")

  private def get(uri: Uri): IO[Request[IO]] = Method.GET(
    uri,
    Authorization(
      Credentials.Token(AuthScheme.Bearer, accessProps.token)
    ),
    Accept(MediaType.application.json),
    Header("x-origin", "https://explorer.apis.google.com")
  )

  def getPlayList(playlistId: String) = {
    val request = get(playListItemsUri(playlistId))
    client.expect[YoutubeDataPlaylistItems](request).attempt
  }

}
