import { User } from '../models/user.model';
import { RefreshToken } from '../models/refresh-token.model';
import { EntityRepository, Repository } from 'typeorm';

@EntityRepository(RefreshToken)
export class RefreshTokensRepository extends Repository<RefreshToken> {
  public constructor() {
    super();
  }

  public async createRefreshToken(
    user: User,
    ttl: number,
  ): Promise<RefreshToken> {
    const expiration = new Date();
    expiration.setTime(expiration.getTime() + ttl);

    const token = new RefreshToken();
    token.user_id = user.id;
    token.is_revoked = false;
    token.expires = expiration;

    return this.save(token);
  }

  public async findTokenById(id: string): Promise<RefreshToken | null> {
    return this.findOne({
      where: {
        id,
      },
    });
  }
}
