package com.minosiants.youtube.extra.data.youtube

import java.time.Instant

final case class SubscriptionResourceId(channelId: String)

final case class SubscriptionSnippet(
    publishedAt: Instant,
    title: String,
    description: String,
    resourceId: SubscriptionResourceId,
    channelId: String,
    thumbnails: Thumbnails
)

final case class SubscriptionContentDetails(
    totalItemCount: Long,
    newItemCount: Long,
    activityType: String
)

final case class Subscription(
    id: String,
    snippet: SubscriptionSnippet,
    contentDetails: SubscriptionContentDetails
)

object Subscription extends Codecs
