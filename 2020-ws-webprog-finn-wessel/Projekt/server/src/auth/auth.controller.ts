import { Body, Controller, Get, Post, Req, UseGuards } from '@nestjs/common';

import {
  RegisterRequest,
  LoginRequest,
  RefreshRequest,
  LogoutRequest,
} from './dto/requests.dto';
import { JWTGuard } from './guards/jwt.guard';
import { ApiOperation, ApiTags } from '@nestjs/swagger';
import { AuthService } from './auth.service';

@ApiTags('authentication')
@Controller('/auth')
export class AuthController {
  private readonly authService: AuthService;

  public constructor(authService: AuthService) {
    this.authService = authService;
  }

  @ApiOperation({ summary: 'Register new user' })
  @Post('/register')
  public async register(@Body() body: RegisterRequest) {
    return this.authService.registerUser(body);
  }

  @ApiOperation({ summary: 'Login user' })
  @Post('/login')
  public async login(@Body() body: LoginRequest) {
    return this.authService.loginUser(body);
  }

  @ApiOperation({ summary: 'Logout user' })
  @Post('/logout')
  public async logout(@Body() body: LogoutRequest) {
    return this.authService.logoutUser(body);
  }

  @ApiOperation({ summary: 'Refresh access token' })
  @Post('/refresh')
  public async refresh(@Body() body: RefreshRequest) {
    return this.authService.refreshAccessToken(body);
  }

  @ApiOperation({ summary: 'Get user profile' })
  @Get('/profile')
  @UseGuards(JWTGuard)
  public async getUser(@Req() request) {
    const userId = request.user.id;
    return this.authService.getUserProfile(userId);
  }
}
