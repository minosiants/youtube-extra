package com.minosiants
package youtube.extra

import java.time.Instant

final case class YoutubeDataSubResourceId(channelId: String)

final case class YoutubeDataSubSnippet(
    publishedAt: Instant,
    title: String,
    description: String,
    resourceId: YoutubeDataSubResourceId,
    channelId: String,
    thumbnails: YoutubeDataThumbnails
)

final case class YoutubeDataSubContentDetails(
    totalItemCount: Long,
    newItemCount: Long,
    activityType: String
)

final case class YoutubeDataSubscription(
    id: String,
    snippet: YoutubeDataSubSnippet,
    contentDetails: YoutubeDataSubContentDetails
)

object YoutubeDataSubscription extends CommonCodecs {}
