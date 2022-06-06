import { Injectable, UnauthorizedException } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

@Injectable()
export class JWTGuard extends AuthGuard('jwt') {
  handleRequest(err, user, info: Error) {
    if (err || info || !user) {
      //throw err || info || new UnauthorizedException();
      // ToDo: rework Exception
      throw new UnauthorizedException(err || info || 'Token malformed');
    } else {
      return user;
    }
  }
}
