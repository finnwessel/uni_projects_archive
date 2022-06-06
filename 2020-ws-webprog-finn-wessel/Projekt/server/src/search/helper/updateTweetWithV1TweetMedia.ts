export function updateTweetWithV1TweetMedia(tweet: any, result: any) {
  if (tweet && tweet.media) {
    tweet.media.forEach((media) => {
      media.url = extractUrl(result, media.media_key);
    });
    return tweet;
  }
  return null;
}

function extractUrl(result: any, mediaKey: string) {
  if (result && result.extended_entities && result.extended_entities.media) {
    const parsedMediaKey = mediaKey.substring(2, mediaKey.length);
    const media = result.extended_entities.media.find(
      (x) => x.id_str === parsedMediaKey,
    );

    if (media && media.video_info && media.video_info.variants) {
      console.log(media.video_info.variants);
      const mp4Media = media.video_info.variants.find(
        (variants) => variants.content_type === 'video/mp4',
      );
      return mp4Media.url;
    } else {
      return 'none';
    }
  } else {
    return 'none';
  }
}
