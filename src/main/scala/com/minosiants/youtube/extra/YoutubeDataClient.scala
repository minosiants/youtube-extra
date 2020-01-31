package com.minosiants
package youtube.extra

import cats.data.EitherT
import cats.effect.IO
import org.http4s.client.dsl.io._
import org.http4s._
import org.http4s.client.Client
import org.http4s.headers.{ Accept, Authorization }

final case class YoutubeDataAccessProps(key: String, token: String)

final case class YoutubeDataClient(
    client: Client[IO],
    apiUri: Uri,
    accessProps: YoutubeDataAccessProps
) {

  private def playListItemsUri(playlistId: String): Uri =
    apiUri / "playlistItems" +? ("key", accessProps.key) +? ("playlistId", playlistId) +? ("part", "snippet")

  private def videosStatisticsUri(ids: List[String]): Uri =
    apiUri / "videos" +? ("key", accessProps.key) +? ("id", ids.mkString(",")) +? ("part", "snippet,statistics")

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

  def getVideos(ids: List[String]) = {
    val request = get(videosStatisticsUri(ids))
    client.expect[YoutubeDataVideos](request).attempt
  }

  def getPlaylistVideos(playlistId: String) = {
    (for {
      playlist <- EitherT(getPlayList(playlistId))
      ids = playlist.items
        .filter(_.notPrivate)
        .map(_.snippet.resourceId.videoId)
      videos <- EitherT(getVideos(ids))
    } yield videos).value

  }
}

object YoutubeDataClient {
  val apiUri = Uri.unsafeFromString("https://www.googleapis.com/youtube/v3")
}
