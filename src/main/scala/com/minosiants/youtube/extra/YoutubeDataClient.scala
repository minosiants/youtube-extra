package com.minosiants
package youtube.extra

import cats.effect.IO
import cats.instances.list._
import cats.syntax.traverse._
import com.minosiants.youtube.extra.data._
import com.minosiants.youtube.extra.data.youtube.{ Subscription => YTSub, _ }
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.headers.{ Accept, Authorization }
import org.http4s.{ EntityDecoder, _ }

final case class YoutubeDataAccessProps(key: String, token: String)

object YoutubeDataAccessProps {
  def props(key: String, token: String) = YoutubeDataAccessProps(key, token)
}

final case class YoutubeDataClient(
    client: Client[IO],
    apiUri: Uri,
    accessProps: YoutubeDataAccessProps
) {

  private val url = Url(apiUri, accessProps.key)

  def getPlaylists(playlistId: String): IO[Playlist] = {

    val request = get(url.playlists(playlistId))

    client.expect[Page[Playlist]](request).flatMap { playlists =>
      playlists.items.headOption match {
        case Some(playlist) => IO(playlist)
        case None =>
          IO.raiseError(PlaylistNotFound(playlistId))
      }

    }
  }

  def getPlaylistItems(playlistId: String): IO[List[Item]] =
    goThroughPages[Item](url.playlistItems(playlistId))

  def getOnePagePlaylistItems(playlistId: String): IO[List[Item]] =
    page[Item](get(url.playlistItems(playlistId))).map(_.items)

  def getVideos(ids: List[String]): IO[List[Video]] =
    goThroughPages[Video](url.videos(ids))

  def getSubscriptions(channelId: String): IO[List[YTSub]] =
    goThroughPages[YTSub](url.subscriptions(channelId))

  def getChannels(ids: List[String]): IO[List[Channel]] =
    goThroughPages[Channel](url.channels(ids))

  def getChannel(id: String): IO[Channel] =
    goThroughPages[Channel](url.channels(id)).flatMap { channels =>
      channels.headOption match {
        case Some(c) => IO(c)
        case None    => Error.channelNotFound(id)
      }
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

  private def subscription(
      sub: YTSub,
      channel: Channel
  ): IO[Subscription] = {
    val uploads = channel.contentDetails.relatedPlaylists.uploads
    for {
      items <- getOnePagePlaylistItems(uploads)
      ids = items.filter(_.notPrivate).map(_.snippet.resourceId.videoId)
      videos <- getVideos(ids.take(3))
    } yield Subscription(sub, videos)

  }

  private def channelBySub(
      subs: List[YTSub],
      channels: List[Channel]
  ): List[(YTSub, Channel)] = {
    (for {
      sub <- subs
      channelId = sub.snippet.resourceId.channelId
      channel   = channels.find(_.id == channelId)
    } yield channel.map((sub, _)).toList).flatten
  }

  def getSubsActivity(channelId: String): IO[Subscriptions] =
    for {
      owner <- getChannel(channelId)
      subs  <- getSubscriptions(channelId)
      ids = subs.map(_.snippet.resourceId.channelId)
      channels <- ids
        .grouped(40)
        .toList
        .map(getChannels)
        .sequence
        .map(_.flatten)
      subAndChannel = channelBySub(subs, channels)
      result <- subAndChannel.map((subscription _).tupled).sequence
    } yield data.Subscriptions(owner, result)

  private def get(uri: Uri): IO[Request[IO]] = Method.GET(
    uri,
    Authorization(
      Credentials.Token(AuthScheme.Bearer, accessProps.token)
    ),
    Accept(MediaType.application.json),
    Header("x-origin", "https://explorer.apis.google.com")
  )

  private def page[A](request: IO[Request[IO]])(
      implicit entityDecoder: EntityDecoder[IO, Page[A]]
  ): IO[Page[A]] = {
    client.expect[Page[A]](request)
  }

  private def goThroughPages[A](uri: Uri)(
      implicit entityDecoder: EntityDecoder[IO, Page[A]]
  ): IO[List[A]] = {

    def go(request: IO[Request[IO]]): IO[List[A]] =
      for {
        resp <- page(request)
        result <- resp.nextPageToken match {
          case Some(pageToken) =>
            val nextPageReq = get(uri +? ("pageToken", pageToken))
            go(nextPageReq).map(_ ++ resp.items)
          case None => IO(resp.items)
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
