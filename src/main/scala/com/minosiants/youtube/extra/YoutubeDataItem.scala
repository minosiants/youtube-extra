package com.minosiants
package youtube.extra

final case class YoutubeDataResourceId(kind: String, videoId: String)
final case class YoutubeDataSnippet(
    resourceId: YoutubeDataResourceId,
    thumbnails: Option[YoutubeDataThumbnails]
)

final case class YoutubeDataItem(id: String, snippet: YoutubeDataSnippet) {
  def notPrivate: Boolean = snippet.thumbnails.nonEmpty
}
