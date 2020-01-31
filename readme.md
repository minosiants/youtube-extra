
# youtube-extra
Goal is to collect some extra useful functionality using youtube data api

### Generate playlist videos html page
Main goal is to be able to sort playlist videos by videos statistics data (views, likes, dislikes, comments) 
 

### How to use

TODO

https://developers.google.com/youtube/v3/docs/playlistItems/list
https://developers.google.com/youtube/v3/docs/videos/list

https://content.googleapis.com/youtube/v3/playlistItems?maxResults=25&part=snippet%2CcontentDetails&playlistId=PLBCF2DAC6FFB574DE&key=AIzaSyAa8yy0GdcGPHdtD083HiGGx_S0vMPScDM




curl \
  'https://www.googleapis.com/youtube/v3/playlistItems?key=[YOUR_API_KEY]' \
  --header 'Authorization: Bearer [YOUR_ACCESS_TOKEN]' \
  --header 'Accept: application/json' \
  --compressed