jest.mock('expo-secure-store', () => ({
  setItemAsync: jest.fn(),
  getItemAsync: jest.fn(),
  deleteItemAsync: jest.fn(),
}));

import * as SecureStore from 'expo-secure-store';
import { tokenStorage } from '../src/features/auth/tokenStorage';

const mockSetItemAsync = jest.mocked(SecureStore.setItemAsync);
const mockGetItemAsync = jest.mocked(SecureStore.getItemAsync);
const mockDeleteItemAsync = jest.mocked(SecureStore.deleteItemAsync);

describe('tokenStorage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('saves, restores, and clears the JWT securely', async () => {
    mockGetItemAsync.mockResolvedValue('stored-jwt');

    await tokenStorage.save('new-jwt');
    await expect(tokenStorage.read()).resolves.toBe('stored-jwt');
    await tokenStorage.clear();

    expect(mockSetItemAsync).toHaveBeenCalledWith('backmind.authToken', 'new-jwt');
    expect(mockGetItemAsync).toHaveBeenCalledWith('backmind.authToken');
    expect(mockDeleteItemAsync).toHaveBeenCalledWith('backmind.authToken');
  });
});
