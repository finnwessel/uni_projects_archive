import {
  UnprocessableEntityException,
  Injectable,
  HttpException,
  HttpStatus,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { SignOptions, TokenExpiredError } from 'jsonwebtoken';
import { User } from '../models/user.model';
import { RefreshToken } from '../models/refresh-token.model';
import { UsersRepository } from '../users/users.repository';
import { RefreshTokensRepository } from './refresh-tokens.repository';
import { ConfigService } from '@nestjs/config';

export interface RefreshTokenPayload {
  jti: string;
  sub: string;
}

@Injectable()
export class TokensService {
  public constructor(
    private readonly configService: ConfigService,
    private readonly refreshTokensRepository: RefreshTokensRepository,
    private readonly usersRepository: UsersRepository,
    private readonly jwtService: JwtService,
  ) {}

  private readonly envTokenSettings = {
    issuer: this.configService.get<string>('JWT_ISSUER'),
    audience: this.configService.get<string>('JWT_AUDIENCE'),
  };

  public async generateAccessToken(user: User): Promise<string> {
    const signOptions: SignOptions = {
      ...this.envTokenSettings,
      subject: String(user.id),
    };

    return this.jwtService.signAsync({}, signOptions);
  }

  public async generateRefreshToken(
    user: User,
    expiresIn: number,
  ): Promise<string> {
    const token = await this.refreshTokensRepository.createRefreshToken(
      user,
      expiresIn,
    );

    const signOptions: SignOptions = {
      ...this.envTokenSettings,
      expiresIn,
      subject: String(user.id),
      jwtid: String(token.id),
    };

    return this.jwtService.signAsync({}, signOptions);
  }

  public async resolveRefreshToken(
    refresh_token: string,
  ): Promise<{ user: User; token: RefreshToken }> {
    const payload = await this.decodeRefreshToken(refresh_token);
    const token = await this.getStoredTokenFromRefreshTokenPayload(payload);

    if (token) {
      if (token.is_revoked) {
        throw new UnauthorizedException('The Refresh token is revoked');
      }
      const user = await this.getUserFromRefreshTokenPayload(payload);

      if (user) {
        return { user, token };
      } else {
        throw new UnprocessableEntityException('Refresh token malformed');
      }
    } else {
      throw new NotFoundException('Could not find given refresh token');
    }
  }

  public async createAccessTokenFromRefreshToken(
    refresh_token: string,
  ): Promise<{ access_token: string; user: User }> {
    const { user } = await this.resolveRefreshToken(refresh_token);

    const access_token = await this.generateAccessToken(user);

    return { user, access_token };
  }

  public async revokeRefreshToken(refresh_token: string) {
    const { jti } = await this.decodeRefreshToken(refresh_token);

    if (jti) {
      const db_refresh_token = await this.refreshTokensRepository.findTokenById(
        jti,
      );
      if (db_refresh_token) {
        db_refresh_token.is_revoked = true;
        this.refreshTokensRepository.save(db_refresh_token).catch(() => {
          throw new HttpException(
            'Failed to revoke refresh token',
            HttpStatus.INTERNAL_SERVER_ERROR,
          );
        });
      } else {
        throw new NotFoundException('Could not find given refresh token');
      }
    } else {
      throw new UnprocessableEntityException('Refresh token malformed');
    }
  }

  private async decodeRefreshToken(
    refresh_token: string,
  ): Promise<RefreshTokenPayload> {
    try {
      return this.jwtService.verifyAsync(refresh_token);
    } catch (e) {
      if (e instanceof TokenExpiredError) {
        throw new UnauthorizedException('Refresh token expired');
      } else {
        throw new UnprocessableEntityException('Refresh token malformed');
      }
    }
  }

  private async getUserFromRefreshTokenPayload(
    payload: RefreshTokenPayload,
  ): Promise<User> {
    const sub = payload.sub;

    if (sub) {
      return this.usersRepository.findWithId(sub);
    } else {
      throw new UnprocessableEntityException('Refresh token malformed');
    }
  }

  private async getStoredTokenFromRefreshTokenPayload(
    payload: RefreshTokenPayload,
  ): Promise<RefreshToken | null> {
    const jti = payload.jti;

    if (jti) {
      return this.refreshTokensRepository.findTokenById(jti);
    } else {
      throw new UnprocessableEntityException('Refresh token malformed');
    }
  }
}
