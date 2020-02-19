package com.minosiants
package youtube.extra

import org.http4s.Uri

case class Url(apiHost: Uri, key: String) {

  private val _maxResult = 50

  private def endpoint(path: String, maxResult: Int): Uri =
    apiHost / path +? ("key", key) +? ("maxResults", maxResult)

  def playlists(playlistId: String): Uri =
    endpoint("playlists", 1) +? ("id", playlistId) +? ("part", "snippet")

  def playlistItems(playlistId: String, maxResult: Int = _maxResult): Uri =
    endpoint("playlistItems", maxResult) +? ("playlistId", playlistId) +? ("part", "snippet")

  def videos(ids: List[String], maxResult: Int = _maxResult): Uri =
    endpoint("videos", maxResult) +? ("id", ids.mkString(",")) +? ("part", "snippet,statistics,contentDetails")

  def subscriptions(channelId: String, maxResult: Int = _maxResult): Uri =
    endpoint("subscriptions", maxResult) +? ("channelId", channelId) +? ("part", "snippet,contentDetails")

  def channels(ids: List[String], maxResult: Int = _maxResult): Uri =
    endpoint("channels", maxResult) +? ("id", ids.mkString(",")) +? ("part", "contentDetails")

  def channels(id: String): Uri = channels(List(id), 1)

}
