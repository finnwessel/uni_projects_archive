export function buildFilterString({ images, videos, retweets }) {
  let filterString = '';
  if (typeof images !== 'undefined') {
    filterString += images === true ? ' has:images ' : ' -has:images ';
  }
  if (typeof videos !== 'undefined') {
    filterString += videos === true ? ' has:videos ' : ' -has:videos ';
  }
  if (typeof retweets !== 'undefined') {
    filterString += retweets === true ? ' is:retweet ' : ' -is:retweet ';
  }
  return filterString;
}
