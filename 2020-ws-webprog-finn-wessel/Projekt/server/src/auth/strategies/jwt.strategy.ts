import { Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { UsersService } from '../../users/users.service';
import { User } from '../../models/user.model';
import { ConfigService } from '@nestjs/config';

export interface AccessTokenPayload {
  sub: string;
}

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  private usersService: UsersService;

  public constructor(usersService: UsersService, configService: ConfigService) {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: `${configService.get('JWT_SECRET_KEY')}`,
      signOptions: {
        expiresIn: '5m',
      },
    });

    this.usersService = usersService;
  }

  async validate(payload: AccessTokenPayload): Promise<User> {
    const { sub: id } = payload;

    const user = await this.usersService.findWithId(id);

    if (user) {
      return user;
    } else {
      return null;
    }
  }
}
