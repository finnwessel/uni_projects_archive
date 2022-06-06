import { Tweet } from '../models/tweet.model';

const parseStreamTweets = function (tweet) {
  if (tweet && tweet.data) {
    const resTweet = new Tweet();
    resTweet.id = tweet.data.id;
    resTweet.author = getAuthor(tweet, tweet.data.author_id);
    resTweet.text = tweet.data.text;
    resTweet.urls = getUrls(tweet);
    resTweet.media = getMedia(tweet);
    resTweet.public_metrics = tweet.data.public_metrics;
    resTweet.possibly_sensitive = tweet.data.possibly_sensitive;
    resTweet.created_at = tweet.data.created_at;
    return resTweet;
  } else {
    return null;
  }
};

function getUrls(tweet) {
  if (tweet.data.entities && tweet.data.entities.urls) {
    return tweet.data.entities.urls;
  } else {
    return [];
  }
}

function getAuthor(tweet, authorId) {
  return tweet.includes.users.find((x) => x.id === authorId);
}

function getMedia(tweet) {
  if (
    typeof tweet.includes !== 'undefined' &&
    typeof tweet.includes.media !== 'undefined'
  ) {
    return tweet.includes.media;
  } else {
    return [];
  }
}
export default parseStreamTweets;
