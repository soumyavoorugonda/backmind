import { apiClient } from '../../api/client';
import { Credentials } from './CredentialsForm';

export type LoginResponse = { token: string };
export type SignupResponse = { id: string; email: string };

export const authApi = {
  login(credentials: Credentials) {
    return apiClient<LoginResponse>('/api/auth/login', {
      body: credentials,
      method: 'POST',
    });
  },

  signup(credentials: Credentials) {
    return apiClient<SignupResponse>('/api/auth/signup', {
      body: credentials,
      method: 'POST',
    });
  },

  logout(token: string) {
    return apiClient<void>('/api/auth/logout', { method: 'POST', token });
  },
};
