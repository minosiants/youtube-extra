package com.minosiants
package youtube.extra

import java.time.Instant

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
