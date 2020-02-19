package com.minosiants.youtube.extra.data.youtube

final case class Thumbnail(
    url: String,
    width: Option[Int],
    height: Option[Int]
)

final case class Thumbnails(
    default: Thumbnail,
    medium: Thumbnail,
    high: Thumbnail,
    standard: Option[Thumbnail]
)
