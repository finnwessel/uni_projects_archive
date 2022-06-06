import { InjectRepository } from '@nestjs/typeorm';
import { Tweet } from '../models/tweet.model';
import { EntityRepository, MoreThan, Repository } from 'typeorm';
import { Search } from '../models/search.model';

@EntityRepository(Search)
export class SearchRepository extends Repository<Search> {
  constructor(
    @InjectRepository(Tweet)
    private readonly tweetsRepo: Repository<Tweet>,
  ) {
    super();
  }

  async createAndSaveSearch(
    userId: string,
    keywords: string,
    keywordsUnified: string,
    images: boolean,
    videos: boolean,
    retweets: boolean,
    tweets: Tweet[],
  ) {
    return new Promise((resolve, reject) => {
      const search = new Search();
      search.keywords = keywords;
      search.keywordsUnified = keywordsUnified;
      search.images = images;
      search.videos = videos;
      search.retweets = retweets;
      search.tweets = tweets;

      this.save(search)
        .then((res) => {
          this.connectUserWithSearch(userId, res.id);
          resolve({
            id: res.id,
            tweets: res.tweets,
          });
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  connectUserWithSearch(userId: string, searchId: string) {
    this.query(
      `INSERT INTO user_searches_search (userId,searchId) VALUES ("${userId}","${searchId}") ON DUPLICATE KEY UPDATE userId = "${userId}", searchId = "${searchId}";`,
    ).catch((error) => console.log(error));
  }

  getCachedSearches(
    keywordsUnified: string,
    maxAgeInMinutes: number,
  ): Promise<Search> {
    const minCreatedDate = new Date(Date.now() - maxAgeInMinutes * 60000);
    return this.findOne({
      where: {
        keywordsUnified: keywordsUnified,
        created_at: MoreThan(minCreatedDate),
      },
      order: {
        created_at: 'ASC',
      },
      relations: [
        'tweets',
        'tweets.author',
        'tweets.urls',
        'tweets.media',
        'tweets.public_metrics',
      ],
    });
  }

  async getSearchWithId(id: string) {
    return this.findOne(id, {
      select: ['id', 'keywords', 'images', 'videos', 'retweets', 'created_at'],
      relations: [
        'tweets',
        'tweets.author',
        'tweets.urls',
        'tweets.media',
        'tweets.public_metrics',
      ],
    });
  }

  async getSearchSettingsWithId(id: string) {
    return this.findOne(id, {
      select: ['id', 'keywords', 'images', 'videos', 'retweets', 'created_at'],
    });
  }
}
