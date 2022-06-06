import { Module } from '@nestjs/common';
import { JwtModule } from '@nestjs/jwt';
import { UsersModule } from '../users/users.module';
import { RefreshToken } from '../models/refresh-token.model';
import { TokensService } from './tokens.service';
import { RefreshTokensRepository } from './refresh-tokens.repository';
import { AuthController } from './auth.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { JwtStrategy } from './strategies/jwt.strategy';
import { AuthService } from './auth.service';

@Module({
  imports: [
    TypeOrmModule.forFeature([RefreshToken, RefreshTokensRepository]),
    JwtModule.registerAsync({
      useFactory: async () => ({
        secret: process.env.JWT_SECRET_KEY,
        signOptions: {
          expiresIn: '90s',
        },
      }),
    }),
    UsersModule,
  ],
  controllers: [AuthController],
  providers: [TokensService, JwtStrategy, AuthService],
})
export class AuthModule {}
