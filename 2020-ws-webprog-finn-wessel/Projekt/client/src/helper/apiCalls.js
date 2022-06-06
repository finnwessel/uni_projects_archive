export function getParamsFromSearch(id, jwt) {
  return fetch(`http://localhost:3000/search/${id}/settings`, {
    headers: new Headers({
      Authorization: "Bearer " + jwt
    })
  }).then(response => response.json());
}

export function getParamsFromStream(id, jwt) {
  return fetch(`http://localhost:3000/stream/${id}/settings`, {
    headers: new Headers({
      Authorization: "Bearer " + jwt
    })
  }).then(response => response.json());
}

export function getTweetsFromRecentSearch(keywords, options, jwt) {
  return fetch(
    `http://localhost:3000/search?keywords=${keywords}&take=${options.take}` +
      buildFilterQuery(options),
    {
      headers: new Headers({
        Authorization: "Bearer " + jwt
      })
    }
  ).then(response => response.json());
}

function buildFilterQuery({ images, videos, retweet }) {
  let filterQuery = "";
  if (images !== 0 || images !== null) {
    filterQuery += (images === 1 || images === true) ? "&images=true" : "&images=false";
  }
  if (videos !== 0 || videos !== null) {
    filterQuery += (videos === 1 || videos === true) ? "&videos=true" : "&videos=false";
  }
  if (retweet !== 0 || retweet !== null) {
    filterQuery += (retweet === 1 || retweet === true) ? "&retweets=true" : "&retweets=false";
  }
  return filterQuery;
}

export function getTweetsFromStream(keywords) {
  return new EventSource(`http://localhost:3000/stream?keywords=${keywords}`);
}

export function getTweetMediaFromTweetWithId(id, jwt) {
  return fetch(`http://localhost:3000/search/tweet/${id}/media`, {
    headers: new Headers({
      Authorization: "Bearer " + jwt
    })
  }).then(response => response.json());
}
