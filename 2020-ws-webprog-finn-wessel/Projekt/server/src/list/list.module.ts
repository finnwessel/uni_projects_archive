import { Module } from '@nestjs/common';
import { ListController } from './list.controller';
import { ListService } from './list.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ListRepository } from './list.repository';
import { List } from '../models/list.model';
import { Tweet } from '../models/tweet.model';
import { SearchRepository } from '../search/search.repository';
import { StreamRepository } from '../stream/stream.repository';

@Module({
  imports: [
    TypeOrmModule.forFeature([
      List,
      Tweet,
      ListRepository,
      SearchRepository,
      StreamRepository,
    ]),
  ],
  controllers: [ListController],
  providers: [ListService],
})
export class ListModule {}
