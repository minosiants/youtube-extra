package com.minosiants.youtube.extra.data.youtube

final case class ResourceId(kind: String, videoId: String)
final case class Snippet(
    resourceId: ResourceId,
    thumbnails: Option[Thumbnails]
)

final case class Item(id: String, snippet: Snippet) {
  def notPrivate: Boolean = snippet.thumbnails.nonEmpty
}
