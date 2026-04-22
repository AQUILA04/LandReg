export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  refreshToken: string;
  id: number;
  username: string;
  email: string;
  roles: string[];
  profil: {
    id: number;
    name: string;
    description: string;
    permissions: string[];
  };
}

export interface LoginRequest {
  username?: string;
  password?: string;
}
