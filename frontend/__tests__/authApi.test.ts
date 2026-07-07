jest.mock('../src/api/client', () => ({ apiClient: jest.fn() }));

import { apiClient } from '../src/api/client';
import { authApi } from '../src/features/auth/authApi';

describe('authApi', () => {
  it('maps login and signup to their backend contracts', async () => {
    jest.mocked(apiClient).mockResolvedValueOnce({ token: 'jwt' }).mockResolvedValueOnce({
      id: 'user-1',
      email: 'new@example.com',
    });

    await expect(
      authApi.login({ email: 'demo@example.com', password: 'secret' }),
    ).resolves.toEqual({ token: 'jwt' });
    await authApi.signup({ email: 'new@example.com', password: 'secret' });

    expect(apiClient).toHaveBeenNthCalledWith(1, '/api/auth/login', {
      body: { email: 'demo@example.com', password: 'secret' },
      method: 'POST',
    });
    expect(apiClient).toHaveBeenNthCalledWith(2, '/api/auth/signup', {
      body: { email: 'new@example.com', password: 'secret' },
      method: 'POST',
    });
  });
});
