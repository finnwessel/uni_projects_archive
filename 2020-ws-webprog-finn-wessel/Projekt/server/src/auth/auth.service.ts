import { Injectable, UnauthorizedException } from '@nestjs/common';
import { UsersService } from '../users/users.service';
import { TokensService } from './tokens.service';
import { User } from '../models/user.model';
import { AuthenticationPayload } from './interfaces/responses.interface';
import { ConfigService } from '@nestjs/config';
import { compare } from 'bcrypt';

@Injectable()
export class AuthService {
  public constructor(
    private readonly configService: ConfigService,
    private readonly usersService: UsersService,
    private readonly tokensService: TokensService,
  ) {}

  private readonly refreshTokeExpiresInDays = this.configService.get<number>(
    'JWT_REFRESH_TOKEN_EXPIRES_IN_DAYS',
    30,
  );

  public async registerUser(body) {
    const user = await this.usersService.registerUser(body);

    const access_token = await this.tokensService.generateAccessToken(user);
    const refresh_token = await this.tokensService.generateRefreshToken(
      user,
      60 * 60 * 24 * this.refreshTokeExpiresInDays,
    );

    const responseData = AuthService.generateResponseData(
      user,
      access_token,
      refresh_token,
    );

    return {
      data: responseData,
    };
  }

  public async loginUser(body) {
    const { identifier, password } = body;

    const user = await this.usersService.findWithIdentifier(identifier);

    const validCredentials = user
      ? await compare(password, user.password)
      : false;

    if (validCredentials) {
      const access_token = await this.tokensService.generateAccessToken(user);

      const refresh_token = await this.tokensService.generateRefreshToken(
        user,
        60 * 60 * 24 * this.refreshTokeExpiresInDays,
      );

      delete user.password;
      const responseData = AuthService.generateResponseData(
        user,
        access_token,
        refresh_token,
      );

      return {
        data: responseData,
      };
    } else {
      throw new UnauthorizedException('The login is invalid');
    }
  }

  public async logoutUser(body) {
    return await this.tokensService.revokeRefreshToken(body.refresh_token);
  }

  public async refreshAccessToken(body) {
    const {
      user,
      access_token,
    } = await this.tokensService.createAccessTokenFromRefreshToken(
      body.refresh_token,
    );

    delete user.password;
    const responseData = AuthService.generateResponseData(user, access_token);

    return {
      data: responseData,
    };
  }

  public async getUserProfile(userId: string) {
    const user = await this.usersService.findWithId(userId);
    delete user.password;
    return {
      data: user,
    };
  }
  // Quelle Nestjs Authentication
  private static generateResponseData(
    user: User,
    accessToken: string,
    refreshToken?: string,
  ): AuthenticationPayload {
    return {
      user: user,
      payload: {
        access_token: accessToken,
        ...(refreshToken ? { refresh_token: refreshToken } : {}),
      },
    };
  }
  // Quelle Ende: Nestjs Authentication
}
