export interface LoginCredentials {
  username: string;
  password: string;
}

export interface LoginData {
  id: string;
  firstname: string;
  lastname: string;
  username: string;
  token: string;
}

export interface RegisterCredentials {
  username: string;
  password: string;
  email: string;
  firstname: string;
  lastname: string;
}

export interface RegisterData extends RegisterCredentials {
  id: string;
}

export interface Account {
  id: string;
  email: string;
  firstname: string;
  lastname: string;
  username: string;
  token: string;
}
