jest.mock('../src/api/client', () => ({ apiClient: jest.fn() }));

import { apiClient } from '../src/api/client';
import { noteApi } from '../src/features/notes/noteApi';

describe('noteApi', () => {
  beforeEach(() => jest.clearAllMocks());

  it('creates notes, loads the today feed, and submits reviews with a token', async () => {
    jest.mocked(apiClient).mockResolvedValue({});

    await noteApi.create('jwt', { content: 'Remember this', category: 'Learning' });
    await noteApi.today('jwt');
    await noteApi.review('jwt', 'note-1', 'USEFUL');
    await noteApi.findAll('jwt');

    expect(apiClient).toHaveBeenNthCalledWith(1, '/api/notes', {
      body: { content: 'Remember this', category: 'Learning' },
      method: 'POST',
      token: 'jwt',
    });
    expect(apiClient).toHaveBeenNthCalledWith(2, '/api/feed/today', { token: 'jwt' });
    expect(apiClient).toHaveBeenNthCalledWith(3, '/api/notes/note-1/review', {
      body: { feedbackType: 'USEFUL', userResponse: null },
      method: 'POST',
      token: 'jwt',
    });
    expect(apiClient).toHaveBeenNthCalledWith(4, '/api/notes', { token: 'jwt' });
  });

  it('serializes an empty optional category as null', async () => {
    jest.mocked(apiClient).mockResolvedValue({});

    await noteApi.create('jwt', { content: 'Remember this', category: '' });

    expect(apiClient).toHaveBeenCalledWith('/api/notes', {
      body: { content: 'Remember this', category: null },
      method: 'POST',
      token: 'jwt',
    });
  });
});
