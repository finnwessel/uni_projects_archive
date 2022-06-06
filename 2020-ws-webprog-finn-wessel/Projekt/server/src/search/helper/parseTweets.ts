import { type } from 'os';

const parseTweets = function (tweets) {
  const parsedTweets = [];

  if (tweets && tweets.data) {
    tweets.data.forEach((tweet) => {
      parsedTweets.push({
        id: tweet.id,
        author: getAuthor(tweets, tweet.author_id),
        text: tweet.text,
        urls: getUrls(tweet),
        media: getMedia(tweets, tweet),
        public_metrics: tweet.public_metrics,
        possibly_sensitive: tweet.possibly_sensitive,
        created_at: tweet.created_at,
      });
    });
  }
  return parsedTweets;
};

function getUrls(tweet) {
  if (tweet.entities && tweet.entities.urls) {
    return tweet.entities.urls;
  } else {
    return [];
  }
}

function getAuthor(tweets, authorId) {
  return tweets.includes.users.find((x) => x.id === authorId);
}

function getMedia(tweets, tweet) {
  const media = [];
  if (
    typeof tweet.attachments !== 'undefined' &&
    typeof tweet.attachments.media_keys !== 'undefined'
  ) {
    tweet.attachments.media_keys.forEach((mediaKey) => {
      const media_object = tweets.includes.media.find(
        (x) => x.media_key === mediaKey,
      );
      if (typeof media_object !== undefined) {
        media.push(media_object);
      }
    });
  }
  return media;
}
export default parseTweets;
