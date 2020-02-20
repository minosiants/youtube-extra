package com.minosiants.youtube.extra.data

import youtube.{ Subscription => YTSub, _ }
import monocle.Traversal
import monocle.function.all._
import monocle.macros.GenLens

final case class Subscription(
    sub: YTSub,
    videos: List[Video]
)

final case class Subscriptions(
    owner: Channel,
    subs: List[Subscription]
)

object Subscriptions extends Codecs {

  val ownerLens   = GenLens[Subscriptions](_.owner)
  val snippetLens = GenLens[Channel](_.snippet)

  val snippetTitleAndDescLens = Traversal
    .apply2[ChannelSnippet, String](_.title, _.description) {
      case (t, d, l) => l.copy(title = t, description = d)
    }

  val ownerTextFieldsLens = ownerLens composeLens snippetLens composeTraversal snippetTitleAndDescLens

  val subsLens   = GenLens[Subscriptions](_.subs)
  val subLens    = GenLens[Subscription](_.sub)
  val subSnippet = GenLens[YTSub](_.snippet)
  val subTitleAndDescTrav = Traversal
    .apply2[SubscriptionSnippet, String](_.title, _.description) {
      case (t, d, l) => l.copy(title = t, description = d)
    }

  val allSubsTraversal = subsLens composeTraversal each

  val subTextFieldsLens =
    allSubsTraversal composeLens subLens composeLens subSnippet composeTraversal subTitleAndDescTrav

  val videosLens = GenLens[Subscription](_.videos)

  val videoSnippetLens = GenLens[Video](_.snippet)

  val videoTextFieldsLens =
    subsLens composeTraversal each composeLens videosLens composeTraversal each composeLens Video.snippetLens composeTraversal Video.textFieldsTrav

}
