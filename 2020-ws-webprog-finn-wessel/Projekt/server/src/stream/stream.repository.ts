import { InjectRepository } from '@nestjs/typeorm';
import { Tweet } from '../models/tweet.model';
import { EntityRepository, Repository } from 'typeorm';
import { Stream } from '../models/stream.model';

@EntityRepository(Stream)
export class StreamRepository extends Repository<Stream> {
  constructor(
    @InjectRepository(Tweet)
    private readonly tweetsRepo: Repository<Tweet>,
  ) {
    super();
  }

  createAndSaveStream(keywords: string): Promise<Stream> {
    const stream = new Stream();
    stream.keywords = keywords;
    return this.save(stream);
  }

  saveTweetWithStreamId(tweet: Tweet, streamId: string) {
    this.tweetsRepo
      .save(tweet)
      .then((res) => {
        this.query(
          `INSERT INTO stream_tweets_tweet (streamId, tweetId) VALUES ("${streamId}", "${res.id}") ON DUPLICATE KEY UPDATE streamId = "${streamId}", tweetId = "${res.id}";`,
        ).catch((error) => console.warn(error));
      })
      .catch((error) => {
        console.debug(error);
      });
  }

  async getStreamSettingsWithId(id: string) {
    return this.findOne(id, {
      select: ['id', 'keywords', 'created_at'],
    });
  }
}
