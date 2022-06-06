import {
  Injectable,
  ConflictException,
} from '@nestjs/common';
import { RegisterRequest } from '../auth/dto/requests.dto';
import { User } from '../models/user.model';
import { UsersRepository } from './users.repository';

@Injectable()
export class UsersService {
  constructor(private readonly userRepo: UsersRepository) {}

  public async registerUser(request: RegisterRequest): Promise<User> {
    const { username, email, password } = request;

    const existingUser = await this.userRepo.findWithUsernameOrEmail(
      username,
      email,
    );

    if (existingUser) {
      throw new ConflictException('Username already in use');
    }
    const user = this.userRepo.create(username, email, password);
    delete (await user).password;
    return user;
  }

  public async findWithId(id: string): Promise<User | null> {
    return this.userRepo.findWithId(id);
  }

  public async findWithIdentifier(identifier: string): Promise<User | null> {
    return this.userRepo.findWithIdentifier(identifier);
  }
}
