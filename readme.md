
# youtube-extra
Goal is to implement some extra useful functionality using youtube data api.

## Functionality available 
### Generate playlist videos html page
Main goal is to be able to sort playlist videos by videos statistics data (views, likes, dislikes, comments) 

### Installation
```
curl -Lo ~/bin/youtube-extra https://raw.githubusercontent.com/minosiants/youtube-extra/master/youtube-extra
chmod 777 ~/bin/youtube-extra
```
 

### How to use

#### playlist
```
youtube-extra playlist -id {playlistId} -t {oauth token}

-id - playlist id from youtube
-t  - oauth token (see instuctons below)

```
example
```
playlist -id PLLMLOC3WM2r5KDwkSRrLJ1_O6kZqlhhFt -t ya29.ImS9ByJmfJwEzLMvajfDecCxMcHUb5QEOhO_ZDi7XELLhcbzFiNb9UzwRcVqVa1KxlQU27z9xFO_m57lK1vkeC17PxVVFuFo_9Nn4YPb00aRHJbNQXhF77KMDS-Qhpo6zOFnnArf 
```

### How to get oauth token
 1. Go to https://tinyurl.com/vltl9p
 2. Execute a request in web console
 3. Copy token from Authorization header
 
![How to get token](docs/images/how-to-get-token.png?raw=true "How to get token")


### Generated sample
[scala days lausanne 2019](docs/scala-days-lausanne-2019)