import { HttpModule, Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Search } from '../models/search.model';
import { Tweet } from '../models/tweet.model';
import { Author } from '../models/author.model';
import { AuthorMetrics } from '../models/authorMetrics.model';
import { Media } from '../models/media.model';
import { TweetMetrics } from '../models/tweetMetrics.model';
import { Url } from '../models/url.model';
import { StreamController } from './stream.controller';
import { StreamService } from './stream.service';
import { TwitterService } from '../helper/twitter.service';
import { Stream } from '../models/stream.model';
import { StreamRepository } from './stream.repository';

@Module({
  imports: [
    HttpModule,
    ConfigModule,
    TypeOrmModule.forFeature([
      Stream,
      Search,
      Tweet,
      Author,
      AuthorMetrics,
      Media,
      TweetMetrics,
      Url,
      StreamRepository,
    ]),
  ],
  controllers: [StreamController],
  providers: [StreamService, TwitterService],
})
export class StreamModule {}
