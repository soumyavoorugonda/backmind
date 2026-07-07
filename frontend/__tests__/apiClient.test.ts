jest.mock('../src/config/env', () => ({
  env: { apiBaseUrl: 'http://localhost:8080' },
}));

import { ApiError, apiClient } from '../src/api/client';

describe('apiClient', () => {
  beforeEach(() => {
    globalThis.fetch = jest.fn();
  });

  it('sends typed JSON requests with bearer authentication', async () => {
    jest.mocked(fetch).mockResolvedValue(
      new Response(JSON.stringify({ id: 'note-1' }), {
        headers: { 'Content-Type': 'application/json' },
        status: 201,
      }),
    );

    const response = await apiClient<{ id: string }>('/api/notes', {
      body: { content: 'Remember this', category: 'Learning' },
      method: 'POST',
      token: 'jwt-token',
    });

    expect(fetch).toHaveBeenCalledWith('http://localhost:8080/api/notes', {
      body: JSON.stringify({ content: 'Remember this', category: 'Learning' }),
      headers: {
        Accept: 'application/json',
        Authorization: 'Bearer jwt-token',
        'Content-Type': 'application/json',
      },
      method: 'POST',
    });
    expect(response).toEqual({ id: 'note-1' });
  });

  it('throws a readable API error for unsuccessful responses', async () => {
    jest.mocked(fetch).mockResolvedValue(
      new Response(JSON.stringify({ message: 'Invalid email or password' }), {
        headers: { 'Content-Type': 'application/json' },
        status: 401,
      }),
    );

    await expect(apiClient('/api/auth/login')).rejects.toEqual(
      new ApiError(401, 'Invalid email or password'),
    );
  });
});
