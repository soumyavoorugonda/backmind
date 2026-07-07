const mockSetItem = jest.fn();
const mockGetItem = jest.fn();
const mockRemoveItem = jest.fn();

Object.defineProperty(globalThis, 'localStorage', {
  configurable: true,
  value: {
    setItem: mockSetItem,
    getItem: mockGetItem,
    removeItem: mockRemoveItem,
  },
});

import { tokenStorage } from '../src/features/auth/tokenStorage.web';

describe('web tokenStorage', () => {
  it('uses browser storage instead of native SecureStore', async () => {
    mockGetItem.mockReturnValue('stored-web-jwt');

    await tokenStorage.save('new-web-jwt');
    await expect(tokenStorage.read()).resolves.toBe('stored-web-jwt');
    await tokenStorage.clear();

    expect(mockSetItem).toHaveBeenCalledWith('backmind.authToken', 'new-web-jwt');
    expect(mockGetItem).toHaveBeenCalledWith('backmind.authToken');
    expect(mockRemoveItem).toHaveBeenCalledWith('backmind.authToken');
  });
});
