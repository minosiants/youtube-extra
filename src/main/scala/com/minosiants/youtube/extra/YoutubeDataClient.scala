package com.minosiants
package youtube.extra

import cats.effect.IO
import org.http4s.client.dsl.io._
import org.http4s._
import org.http4s.client.Client
import org.http4s.headers.{ Accept, Authorization }
import cats.syntax.traverse._
import cats.instances.list._

final case class YoutubeDataAccessProps(key: String, token: String)

object YoutubeDataAccessProps {
  def props(key: String, token: String) = YoutubeDataAccessProps(key, token)
}

final case class YoutubeDataClient(
    client: Client[IO],
    apiUri: Uri,
    accessProps: YoutubeDataAccessProps
) {

  def getPlaylists(playlistId: String): IO[YoutubeDataPlaylist] = {

    val request = get(playlistsUri(playlistId))

    client.expect[YoutubeDataPlaylists](request).flatMap { playlists =>
      playlists.items.headOption match {
        case Some(playlist) => IO(playlist)
        case None =>
          IO.raiseError(PlaylistNotFound(s"Playlist $playlistId not found"))
      }

    }
  }

  def getPlaylistItems(playlistId: String): IO[List[YoutubeDataItem]] = {

    val uri = playlistItemsUri(playlistId)
    val extract =
      (playlist: YoutubeDataPlaylistItems) =>
        (playlist.nextPageToken, playlist.items)
    goThroughPages(uri, extract)

  }

  def getVideos(ids: List[String]): IO[List[YoutubeDataVideo]] = {

    val request = get(videosUri(ids))
    client.expect[YoutubeDataVideos](request)

    val extract =
      (videos: YoutubeDataVideos) => (videos.nextPageToken, videos.items)
    goThroughPages(videosUri(ids), extract)
  }

  def getFullPlaylist(playlistId: String): IO[FullPlaylist] = {
    for {
      playlist      <- getPlaylists(playlistId)
      playlistItems <- getPlaylistItems(playlistId)
      ids = playlistItems
        .filter(_.notPrivate)
        .map(_.snippet.resourceId.videoId)
      videos <- ids.grouped(40).toList.map(getVideos).sequence
    } yield FullPlaylist(playlist, videos.flatten)

  }

  private def playlistsUri(playlistId: String): Uri =
    apiUri / "playlists" +? ("key", accessProps.key) +? ("id", playlistId) +? ("part", "snippet")

  private def playlistItemsUri(playlistId: String): Uri =
    apiUri / "playlistItems" +? ("key", accessProps.key) +? ("playlistId", playlistId) +? ("part", "snippet") +? ("maxResults", 15)

  private def videosUri(ids: List[String]): Uri =
    apiUri / "videos" +? ("key", accessProps.key) +? ("id", ids.mkString(",")) +? ("part", "snippet,statistics,contentDetails") +? ("maxResults", 15)

  private def get(uri: Uri): IO[Request[IO]] = Method.GET(
    uri,
    Authorization(
      Credentials.Token(AuthScheme.Bearer, accessProps.token)
    ),
    Accept(MediaType.application.json),
    Header("x-origin", "https://explorer.apis.google.com")
  )

  private def goThroughPages[A, B](
      uri: Uri,
      extract: B => (Option[String], List[A])
  )(
      implicit entityDecoder: EntityDecoder[IO, B]
  ): IO[List[A]] = {

    def go(request: IO[Request[IO]]): IO[List[A]] =
      for {
        resp <- client.expect[B](request)
        result <- extract(resp) match {
          case (Some(pageToken), items) =>
            val nextPageReq = get(uri +? ("pageToken", pageToken))
            go(nextPageReq).map(_ ++ items)
          case (None, items) => IO(items)
        }
      } yield result

    go(get(uri))
  }
}

object YoutubeDataClient {
  val apiUri = Uri.unsafeFromString("https://www.googleapis.com/youtube/v3")
  //key obtained using googleapis web console
  //https://developers.google.com/youtube/v3/docs/playlistItems/list
  val googleAppKey = "AIzaSyAa8yy0GdcGPHdtD083HiGGx_S0vMPScDM"
}
