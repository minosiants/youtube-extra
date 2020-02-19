package com.minosiants.youtube.extra.data.youtube

import java.time.Instant

final case class YoutubeDataChannelPlaylist(uploads: String)

final case class YoutubeDataChannelContentDetails(
    relatedPlaylists: YoutubeDataChannelPlaylist
)
final case class YoutubeDataChannelSnippet(
    publishedAt: Instant,
    title: String,
    description: String,
    thumbnails: YoutubeDataThumbnails
)

final case class YoutubeDataChannel(
    id: String,
    snippet: YoutubeDataChannelSnippet,
    contentDetails: YoutubeDataChannelContentDetails
)
object YoutubeDataChannel extends CommonCodecs
