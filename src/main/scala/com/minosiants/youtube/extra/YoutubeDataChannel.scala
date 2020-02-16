package com.minosiants
package youtube.extra

final case class YoutubeDataChannelPlaylist(uploads: String)

final case class YoutubeDataChannelContentDetails(
    relatedPlaylists: YoutubeDataChannelPlaylist
)

final case class YoutubeDataChannel(
    id: String,
    contentDetails: YoutubeDataChannelContentDetails
)
