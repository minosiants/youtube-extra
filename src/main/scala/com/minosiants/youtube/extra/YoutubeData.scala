package com.minosiants
package youtube.extra

import java.time.Instant

import cats.effect.IO
import cats.syntax.either._
import io.circe.{ Decoder, Encoder }
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import io.circe.generic.auto._
import monocle.macros.GenLens
import monocle.function.all._
import monocle.Traversal

final case class YoutubeDataThumbnail(url: String, width: Int, height: Int)

final case class YoutubeDataThumbnails(
    default: YoutubeDataThumbnail,
    medium: YoutubeDataThumbnail,
    high: YoutubeDataThumbnail,
    standard: Option[YoutubeDataThumbnail]
)

final case class YoutubeDataResourceId(kind: String, videoId: String)
final case class YoutubeDataSnippet(
    resourceId: YoutubeDataResourceId,
    thumbnails: Option[YoutubeDataThumbnails]
)
final case class YoutubeDataItem(id: String, snippet: YoutubeDataSnippet) {
  def notPrivate: Boolean = snippet.thumbnails.nonEmpty
}
final case class YoutubeDataPageInfo(totalResults: Int, resultsPerPage: Int)

final case class YoutubeDataPlaylistItems(
    nextPageToken: Option[String],
    prevPageToken: Option[String],
    pageInfo: Option[YoutubeDataPageInfo],
    items: List[YoutubeDataItem]
)

final case class YoutubeDataVideoSnippet(
    channelTitle: String,
    publishedAt: Instant,
    channelId: String,
    title: String,
    description: String,
    thumbnails: Option[YoutubeDataThumbnails],
    tags: Option[List[String]]
)
final case class YoutubeDataVideoStatistics(
    viewCount: String,
    likeCount: Option[String],
    dislikeCount: Option[String],
    favoriteCount: String,
    commentCount: Option[String]
)
final case class YoutubeDataVideoContentDetails(
    duration: String,
    dimension: String
)

final case class YoutubeDataVideo(
    id: String,
    snippet: YoutubeDataVideoSnippet,
    contentDetails: YoutubeDataVideoContentDetails,
    statistics: YoutubeDataVideoStatistics
)

final case class YoutubeDataVideos(
    pageInfo: YoutubeDataPageInfo,
    nextPageToken: Option[String],
    prevPageToken: Option[String],
    items: List[YoutubeDataVideo]
)

final case class YoutubeDataPlaylistSnippet(
    publishedAt: Instant,
    channelId: String,
    title: String,
    description: String,
    thumbnails: YoutubeDataThumbnails,
    channelTitle: String
)
final case class YoutubeDataPlaylist(
    id: String,
    snippet: YoutubeDataPlaylistSnippet
)
final case class YoutubeDataPlaylists(
    pageInfo: YoutubeDataPageInfo,
    items: List[YoutubeDataPlaylist]
)

final case class FullPlaylist(
    playlistInfo: YoutubeDataPlaylist,
    videos: List[YoutubeDataVideo]
)

object FullPlaylist extends CommonCodecs {

  val videosLens  = GenLens[FullPlaylist](_.videos)
  val snippetLens = GenLens[YoutubeDataVideo](_.snippet)

  val allVideos
      : Traversal[FullPlaylist, YoutubeDataVideo] = videosLens composeTraversal each

  val titleAndDescription =
    Traversal.apply2[YoutubeDataVideoSnippet, String](_.title, _.description) {
      case (fn, ln, l) => l.copy(title = fn, description = ln)
    }
  val videoTitleAndDescriptionLens = (allVideos composeLens snippetLens composeTraversal titleAndDescription)

  val playlistLens        = GenLens[FullPlaylist](_.playlistInfo)
  val playlistSnippetLens = GenLens[YoutubeDataPlaylist](_.snippet)
  val playlisttitleAndDescription =
    Traversal
      .apply2[YoutubeDataPlaylistSnippet, String](_.title, _.description) {
        case (fn, ln, l) => l.copy(title = fn, description = ln)
      }

  val playlistTitleAndDescriptionLens = (playlistLens composeLens playlistSnippetLens composeTraversal playlisttitleAndDescription)
}

object YoutubeDataPlaylists extends CommonCodecs {
  implicit def decoder: EntityDecoder[IO, YoutubeDataPlaylists] =
    jsonOf[IO, YoutubeDataPlaylists]

}

object YoutubeDataVideos extends CommonCodecs {
  implicit def decoder: EntityDecoder[IO, YoutubeDataVideos] =
    jsonOf[IO, YoutubeDataVideos]

  val itemsLens   = GenLens[YoutubeDataVideos](_.items)
  val snippetLens = GenLens[YoutubeDataVideo](_.snippet)

  val allVideos
      : Traversal[YoutubeDataVideos, YoutubeDataVideo] = itemsLens composeTraversal each

  val titleAndDescription =
    Traversal.apply2[YoutubeDataVideoSnippet, String](_.title, _.description) {
      case (fn, ln, l) => l.copy(title = fn, description = ln)
    }
  val titleAndDescriptionLens = (snippetLens composeTraversal titleAndDescription)

}

object YoutubeDataPlaylistItems extends CommonCodecs {

  implicit def decoder: EntityDecoder[IO, YoutubeDataPlaylistItems] =
    jsonOf[IO, YoutubeDataPlaylistItems]

}

trait CommonCodecs {
  implicit val encodeInstant: Encoder[Instant] =
    Encoder.encodeString.contramap[Instant](_.toString)
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emap {
    str =>
      Either.catchNonFatal(Instant.parse(str)).leftMap(_.toString)
  }
}
