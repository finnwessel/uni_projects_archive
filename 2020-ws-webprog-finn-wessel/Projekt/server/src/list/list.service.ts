import {
  ForbiddenException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { ListRepository } from './list.repository';
import { CreateListArgs } from './dto/createList.args';
import { SearchRepository } from '../search/search.repository';
import { StreamRepository } from '../stream/stream.repository';
import { UpdateListArgs } from './dto/updateList.args';

@Injectable()
export class ListService {
  constructor(
    private readonly listRepository: ListRepository,
    private readonly searchRepository: SearchRepository,
    private readonly streamRepository: StreamRepository,
  ) {}

  getAllListsFromUser(userId: string) {
    return this.listRepository.find({ userId: userId });
  }

  async getListWithId(userId: string, listId: string) {
    const list = await this.listRepository.findOne(
      { id: listId },
      {
        relations: [
          'tweets',
          'tweets.author',
          'tweets.author.public_metrics',
          'tweets.urls',
          'tweets.media',
          'tweets.public_metrics',
        ],
      },
    );
    if (list) {
      if (list.isPublic || list.userId === userId) {
        return list;
      } else {
        throw new ForbiddenException();
      }
    } else {
      throw new NotFoundException();
    }
  }

  async createList(userId: string, createListArgs: CreateListArgs) {
    let result = null;
    if (createListArgs.sourceType == 'search') {
      result = await this.searchRepository
        .createQueryBuilder('search')
        .where({ id: createListArgs.uuid })
        .leftJoin('search.tweets', 'tweets')
        .select(['search.id', 'tweets.id'])
        .getOne()
        .catch((error) => console.log(error));
    } else {
      result = await this.streamRepository
        .createQueryBuilder('stream')
        .where({ id: createListArgs.uuid })
        .leftJoin('stream.tweets', 'tweets')
        .select(['stream.id', 'tweets.id'])
        .getOne()
        .catch((error) => console.log(error));
    }
    if (result && result.tweets) {
      return await this.listRepository
        .createAndSaveList(
          userId,
          createListArgs.name,
          createListArgs.sourceType,
          result.id,
          result.tweets,
        )
        .catch((error) => {
          throw new NotFoundException();
        });
    } else {
      throw new NotFoundException();
    }
  }

  async updateListWithId(
    userId: string,
    listId: string,
    updateList: UpdateListArgs,
  ) {
    const list = await this.listRepository.findOne({ id: listId });
    if (list) {
      if (list.userId === userId) {
        return this.listRepository
          .save({
            id: listId,
            ...updateList,
          })
          .catch((error) => {
            console.log(error);
          });
      } else {
        throw new ForbiddenException();
      }
    } else {
      throw new NotFoundException();
    }
  }

  async deleteListWithId(userId: string, listId: string) {
    const list = await this.listRepository.findOne({ id: listId });
    if (list) {
      if (list.userId === userId) {
        this.listRepository.remove(list).catch((error) => {
          console.log(error);
          throw new InternalServerErrorException('Failed to delete list.');
        });
      } else {
        throw new ForbiddenException();
      }
    } else {
      throw new NotFoundException();
    }
  }
}
