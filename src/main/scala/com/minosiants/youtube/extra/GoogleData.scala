package com.minosiants
package youtube.extra

import java.util.Date


sealed trait GoogleDataThumbnail {
  def url: String
  def width: Int
  def height: Int

}
case class GoogleDataDefault(url: String, width: Int, height: Int)  extends GoogleDataThumbnail
case class GoogleDataMedium(url: String, width: Int, height: Int)   extends GoogleDataThumbnail
case class GoogleDataHigh(url: String, width: Int, height: Int)     extends GoogleDataThumbnail
case class GoogleDataStandard(url: String, width: Int, height: Int) extends GoogleDataThumbnail

object GoogleDataThumbnail {


}


case class GoogleDataResourceId(kind: String, videoId: String)
case class GoogleDataSnippet(
                              resourceId: GoogleDataResourceId,
                              channelTitle: String,
                              playlistId: String,
                              publishedAt: Date,
                              channelId: String,
                              title: String,
                              description: String//,
                              //thumbnails: List[GoogleDataThumbnail]
                            )
case class GoogleDataItem(id: String, snippet:GoogleDataSnippet)
case class GoogleDataPageInfo(totalResults: Int, resultsPerPage: Int)
case class GoogleDataPlaylistItems(
                                    kind: String,
                                    nextPageToken: Option[String],
                                    prevPageToken: Option[String],
                                    pageInfo: GoogleDataPageInfo,
                                    items: List[GoogleDataItem]
                                  )