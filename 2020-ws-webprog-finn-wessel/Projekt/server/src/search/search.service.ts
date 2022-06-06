import { HttpException, HttpStatus, Injectable, Logger } from '@nestjs/common';
import { SearchArgs } from './dto/search.args';
import parseTweets from './helper/parseTweets';
import { ConfigService } from '@nestjs/config';
import { sortKeyWordsAlphabetical } from '../helper/stream';
import { SearchRepository } from './search.repository';
import { TwitterService } from '../helper/twitter.service';
import { buildFilterString } from './helper/helper';
import { InjectRepository } from '@nestjs/typeorm';
import { Tweet } from '../models/tweet.model';
import { Repository } from 'typeorm';
import { updateTweetWithV1TweetMedia } from './helper/updateTweetWithV1TweetMedia';

@Injectable()
export class SearchService {
  constructor(
    private readonly configService: ConfigService,
    private readonly twitterService: TwitterService,
    private readonly searchRepository: SearchRepository,
    @InjectRepository(Tweet)
    private readonly tweetRepository: Repository<Tweet>,
  ) {}

  private readonly logger = new Logger(SearchService.name);

  async search(searchArgs: SearchArgs, userId: string): Promise<any> {
    const parsedSearchArgs = {
      ...searchArgs,
      keywordsUnified: sortKeyWordsAlphabetical(
        searchArgs.keywords +
          buildFilterString({
            images: searchArgs.images,
            videos: searchArgs.videos,
            retweets: searchArgs.retweets,
          }),
      ),
    };
    const cachedSearches = await this.searchRepository.getCachedSearches(
      parsedSearchArgs.keywordsUnified,
      5,
    );
    // if no query matches
    if (cachedSearches) {
      this.searchRepository.connectUserWithSearch(userId, cachedSearches.id);
      this.logger.log(
        `Return cached results for keywords: ${cachedSearches.keywords}`,
      );
      return {
        id: cachedSearches.id,
        tweets: cachedSearches.tweets,
      };
    } else {
      const tweets = await this.searchRecentTwitterTweets(
        parsedSearchArgs.keywordsUnified,
        parsedSearchArgs.take,
      ).catch((error) => {
        this.logger.debug(error);
        throw new HttpException(
          'Failed to fetch Tweets from Twitter Api',
          HttpStatus.INTERNAL_SERVER_ERROR,
        );
      });
      this.logger.log(
        `Return fresh results for keywords: ${parsedSearchArgs.keywords}`,
      );
      return await this.searchRepository.createAndSaveSearch(
        userId,
        parsedSearchArgs.keywords,
        parsedSearchArgs.keywordsUnified,
        parsedSearchArgs.images,
        parsedSearchArgs.videos,
        parsedSearchArgs.retweets,
        tweets,
      );
    }
  }

  private async searchRecentTwitterTweets(
    keywordsUnified: string,
    take: number,
  ): Promise<any> {
    const url = `https://api.twitter.com/2/tweets/search/recent?query=${keywordsUnified}&max_results=${take}&tweet.fields=attachments,author_id,context_annotations,conversation_id,created_at,entities,geo,id,in_reply_to_user_id,lang,possibly_sensitive,public_metrics,referenced_tweets,reply_settings,source,text,withheld&expansions=attachments.poll_ids,attachments.media_keys,author_id,entities.mentions.username,geo.place_id,in_reply_to_user_id,referenced_tweets.id,referenced_tweets.id.author_id&user.fields=created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld&media.fields=duration_ms,height,media_key,preview_image_url,type,url,width,public_metrics&place.fields=contained_within,country,country_code,full_name,geo,id,name,place_type&poll.fields=duration_minutes,end_datetime,id,options,voting_status`;
    const result = await this.twitterService.getFromTwitterApi(url);
    return parseTweets(result);
  }

  async getSearchWithId(id: string) {
    return this.searchRepository.getSearchWithId(id);
  }

  async getSearchSettingsWithId(id: string) {
    return this.searchRepository.getSearchSettingsWithId(id);
  }

  async getVideoFromTweetWithId(id) {
    const tweet = await this.tweetRepository.findOne(id, {
      relations: ['media'],
    });
    if (tweet) {
      const result = await this.twitterService.getFromTwitterApi(
        `https://api.twitter.com/1.1/statuses/show.json?id=${id}`,
      );
      const updatedTweet = updateTweetWithV1TweetMedia(tweet, result);
      this.tweetRepository.save(updatedTweet).catch((error) => {
        this.logger.error(error);
      });
      return updatedTweet;
    }
    return null;
  }
}
