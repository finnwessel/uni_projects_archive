import { HttpModule, Module } from '@nestjs/common';
import { SearchController } from './search.controller';
import { SearchService } from './search.service';
import { ConfigModule } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Tweet } from '../models/tweet.model';
import { AuthorMetrics } from '../models/authorMetrics.model';
import { Media } from '../models/media.model';
import { TweetMetrics } from '../models/tweetMetrics.model';
import { Url } from '../models/url.model';
import { Author } from '../models/author.model';
import { Search } from '../models/search.model';
import { SearchRepository } from './search.repository';
import { TwitterService } from '../helper/twitter.service';

@Module({
  imports: [
    HttpModule,
    ConfigModule,
    TypeOrmModule.forFeature([
      Search,
      Tweet,
      Author,
      AuthorMetrics,
      Media,
      TweetMetrics,
      Url,
      SearchRepository,
    ]),
  ],
  controllers: [SearchController],
  providers: [SearchService, TwitterService],
})
export class SearchModule {}
