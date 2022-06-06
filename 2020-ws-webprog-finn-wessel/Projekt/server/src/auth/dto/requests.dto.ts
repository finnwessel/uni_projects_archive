import { IsEmail, IsJWT, IsNotEmpty, MinLength } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class LoginRequest {
  @ApiProperty({
    example: 'max123',
    description: 'The username or email of the user.',
  })
  @IsNotEmpty({ message: 'A username or email is required' })
  readonly identifier: string;

  @ApiProperty({
    example: '12345678',
    description: 'The password of the the user',
  })
  @IsNotEmpty({ message: 'A password is required to login' })
  readonly password: string;
}

export class LogoutRequest {
  @ApiProperty({
    example: 'header.payload.signature',
    description: 'The refresh token',
  })
  @IsNotEmpty({ message: 'The refresh token is required' })
  @IsJWT()
  readonly refresh_token: string;
}

export class RegisterRequest {
  @ApiProperty({
    example: 'max-mustermann@email.com',
    description: 'The email of the new user',
  })
  @IsNotEmpty({ message: 'An email is required' })
  @IsEmail()
  readonly email: string;

  @ApiProperty({
    example: 'max_mustermann',
    description: 'The username of the new user',
  })
  @IsNotEmpty({ message: 'An username is required' })
  readonly username: string;

  @ApiProperty({
    example: '12345678',
    description: 'The password of the the new user',
  })
  @IsNotEmpty({ message: 'A password is required' })
  @MinLength(6, { message: 'Your password must be at least 6 characters' })
  readonly password: string;
}

export class RefreshRequest {
  @ApiProperty({
    example: 'header.payload.signature',
    description: 'The refresh token',
  })
  @IsNotEmpty({ message: 'The refresh token is required' })
  @IsJWT()
  readonly refresh_token: string;
}
