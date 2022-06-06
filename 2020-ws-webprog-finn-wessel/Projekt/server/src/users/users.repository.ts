import { Injectable } from '@nestjs/common';
import { User } from '../models/user.model';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';

@Injectable()
export class UsersRepository {
  public constructor(
    @InjectRepository(User) private readonly usersRepo: Repository<User>,
  ) {}

  public async findWithId(id: string): Promise<User | null> {
    return this.usersRepo.findOne({
      where: {
        id,
      },
    });
  }

  public async findWithUsernameOrEmail(
    username: string,
    email: string,
  ): Promise<User | null> {
    return this.usersRepo.findOne({
      where: [{ username }, { email }],
    });
  }

  public async findWithIdentifier(identifier: string): Promise<User | null> {
    return this.usersRepo.findOne({
      where: [{ username: identifier }, { email: identifier }],
    });
  }

  public async create(
    username: string,
    email: string,
    password: string,
  ): Promise<User> {
    const user = new User();
    user.username = username;
    user.email = email;
    user.password = password;

    return this.usersRepo.save(user);
  }
}
