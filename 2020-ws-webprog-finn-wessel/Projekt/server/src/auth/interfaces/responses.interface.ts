import { User } from '../../models/user.model';

export interface AuthenticationPayload {
  user: User;
  payload: {
    access_token: string;
    refresh_token?: string;
  };
}
