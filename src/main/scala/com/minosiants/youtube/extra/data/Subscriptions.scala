package com.minosiants.youtube.extra.data

import com.minosiants.youtube.extra.data.youtube.{
  CommonCodecs,
  YoutubeDataChannel,
  YoutubeDataChannelSnippet,
  YoutubeDataSubSnippet,
  YoutubeDataSubscription,
  YoutubeDataVideo
}
import monocle.Traversal
import monocle.function.all._
import monocle.macros.GenLens

final case class Subscription(
    sub: YoutubeDataSubscription,
    videos: List[YoutubeDataVideo]
)

final case class Subscriptions(
    owner: YoutubeDataChannel,
    subs: List[Subscription]
)

object Subscriptions extends CommonCodecs {

  type Snippet = YoutubeDataChannelSnippet

  val ownerLens   = GenLens[Subscriptions](_.owner)
  val snippetLens = GenLens[YoutubeDataChannel](_.snippet)

  val snippetTitleAndDescLens = Traversal
    .apply2[Snippet, String](_.title, _.description) {
      case (t, d, l) => l.copy(title = t, description = d)
    }

  val ownerTitleAndDescriptionLens = ownerLens composeLens snippetLens composeTraversal snippetTitleAndDescLens

  val subsLens   = GenLens[Subscriptions](_.subs)
  val subLens    = GenLens[Subscription](_.sub)
  val subSnippet = GenLens[YoutubeDataSubscription](_.snippet)
  val subTitleAndDescTrav = Traversal
    .apply2[YoutubeDataSubSnippet, String](_.title, _.description) {
      case (t, d, l) => l.copy(title = t, description = d)
    }

  val allSubsTraversal = subsLens composeTraversal each

  val subTitleAndDescriptionLens =
    allSubsTraversal composeLens subLens composeLens subSnippet composeTraversal subTitleAndDescTrav

  val videosLens = GenLens[Subscription](_.videos)

  val videoSnippetLens = GenLens[YoutubeDataVideo](_.snippet)

  val videoTitleAndDescriptionLens =
    subsLens composeTraversal each composeLens videosLens composeTraversal each composeLens YoutubeDataVideo.snippetLens composeTraversal YoutubeDataVideo.titleAndDescriptionTrav

}
