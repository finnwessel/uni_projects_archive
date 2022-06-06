import { InjectRepository } from '@nestjs/typeorm';
import { EntityRepository, Repository } from 'typeorm';
import { List } from '../models/list.model';
import { Search } from '../models/search.model';
import { Tweet } from '../models/tweet.model';

@EntityRepository(List)
export class ListRepository extends Repository<List> {
  constructor(
    @InjectRepository(Search)
    private readonly searchRepo: Repository<Search>,
  ) {
    super();
  }

  async createAndSaveList(
    userId: string,
    name: string,
    sourceType: string,
    sourceId: string,
    tweets: Tweet[],
  ) {
    const list = new List();
    list.name = name;
    list.userId = userId;
    list.sourceType = sourceType;
    list.sourceId = sourceId;
    list.tweets = tweets;
    return this.save(list);
  }
}
