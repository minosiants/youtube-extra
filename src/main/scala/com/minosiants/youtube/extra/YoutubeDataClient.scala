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

    client.expect[YoutubeDataPage[YoutubeDataPlaylist]](request).flatMap {
      playlists =>
        playlists.items.headOption match {
          case Some(playlist) => IO(playlist)
          case None =>
            IO.raiseError(PlaylistNotFound(playlistId))
        }

    }
  }

  def getPlaylistItems(playlistId: String): IO[List[YoutubeDataItem]] =
    goThroughPages[YoutubeDataItem](playlistItemsUri(playlistId))

  def getOnePagePlaylistItems(playlistId: String): IO[List[YoutubeDataItem]] =
    page[YoutubeDataItem](get(playlistItemsUri(playlistId))).map(_.items)

  def getVideos(ids: List[String]): IO[List[YoutubeDataVideo]] =
    goThroughPages[YoutubeDataVideo](videosUri(ids))

  def getSubscriptions(channelId: String): IO[List[YoutubeDataSubscription]] =
    goThroughPages[YoutubeDataSubscription](subUri(channelId))

  def getChannels(ids: List[String]): IO[List[YoutubeDataChannel]] =
    goThroughPages[YoutubeDataChannel](channelsUri(ids))

  def getChannel(id: String): IO[YoutubeDataChannel] =
    goThroughPages[YoutubeDataChannel](channelsUri(List(id))).flatMap {
      channels =>
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
      sub: YoutubeDataSubscription,
      channel: YoutubeDataChannel
  ): IO[Subscription] = {
    val uploads = channel.contentDetails.relatedPlaylists.uploads
    for {
      items <- getOnePagePlaylistItems(uploads)
      ids = items.filter(_.notPrivate).map(_.snippet.resourceId.videoId)
      videos <- getVideos(ids.take(5))
    } yield Subscription(sub, videos)

  }

  private def channelBySub(
      subs: List[YoutubeDataSubscription],
      channels: List[YoutubeDataChannel]
  ): List[(YoutubeDataSubscription, YoutubeDataChannel)] = {
    (for {
      sub <- subs
      channelId = sub.snippet.resourceId.channelId
      channel   = channels.find(_.id == channelId)
    } yield channel.map((sub, _)).toList).flatten
  }

  def getSubsActivity(channelId: String): IO[SubscriptionsActivity] =
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
    } yield SubscriptionsActivity(owner, result)

  private def playlistsUri(playlistId: String): Uri =
    apiUri / "playlists" +? ("key", accessProps.key) +? ("id", playlistId) +? ("part", "snippet")

  private def playlistItemsUri(playlistId: String): Uri =
    apiUri / "playlistItems" +? ("key", accessProps.key) +? ("playlistId", playlistId) +? ("part", "snippet") +? ("maxResults", 50)

  private def videosUri(ids: List[String]): Uri =
    apiUri / "videos" +? ("key", accessProps.key) +? ("id", ids.mkString(",")) +? ("part", "snippet,statistics,contentDetails") +? ("maxResults", 50)

  private def subUri(channelId: String): Uri =
    apiUri / "subscriptions" +? ("key", accessProps.key) +? ("channelId", channelId) +? ("part", "snippet,contentDetails") +? ("maxResults", 50)

  private def channelsUri(ids: List[String]): Uri =
    apiUri / "channels" +? ("key", accessProps.key) +? ("id", ids.mkString(",")) +? ("part", "contentDetails") +? ("maxResults", 50)

  private def get(uri: Uri): IO[Request[IO]] = Method.GET(
    uri,
    Authorization(
      Credentials.Token(AuthScheme.Bearer, accessProps.token)
    ),
    Accept(MediaType.application.json),
    Header("x-origin", "https://explorer.apis.google.com")
  )

  private def page[A](request: IO[Request[IO]])(
      implicit entityDecoder: EntityDecoder[IO, YoutubeDataPage[A]]
  ): IO[YoutubeDataPage[A]] = {
    client.expect[YoutubeDataPage[A]](request)
  }

  private def goThroughPages[A](uri: Uri)(
      implicit entityDecoder: EntityDecoder[IO, YoutubeDataPage[A]]
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
