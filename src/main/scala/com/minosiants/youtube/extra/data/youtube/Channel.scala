package com.minosiants.youtube.extra.data.youtube

import java.time.Instant

final case class ChannelPlaylist(uploads: String)

final case class ChannelContentDetails(
    relatedPlaylists: ChannelPlaylist
)
final case class ChannelSnippet(
    publishedAt: Instant,
    title: String,
    description: String,
    thumbnails: Thumbnails
)

final case class Channel(
    id: String,
    snippet: ChannelSnippet,
    contentDetails: ChannelContentDetails
)
object Channel extends Codecs
