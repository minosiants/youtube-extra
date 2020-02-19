package com.minosiants.youtube.extra.data.youtube

import java.time.Instant

final case class PlaylistSnippet(
    publishedAt: Instant,
    channelId: String,
    title: String,
    description: String,
    thumbnails: Thumbnails,
    channelTitle: String
)

final case class Playlist(
    id: String,
    snippet: PlaylistSnippet
)
