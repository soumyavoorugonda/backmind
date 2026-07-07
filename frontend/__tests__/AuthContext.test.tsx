import { render } from '@testing-library/react-native';
import { Text } from 'react-native';

jest.mock('../src/features/auth/tokenStorage', () => ({
  tokenStorage: {
    read: jest.fn(),
    save: jest.fn(),
    clear: jest.fn(),
  },
}));

import { tokenStorage } from '../src/features/auth/tokenStorage';
import { AuthProvider, useAuth } from '../src/features/auth/AuthContext';

const mockRead = jest.mocked(tokenStorage.read);

function SessionProbe() {
  const { status } = useAuth();
  return <Text>{status}</Text>;
}

describe('AuthProvider', () => {
  it('restores an authenticated session from secure storage', async () => {
    mockRead.mockResolvedValue('stored-jwt');

    const screen = render(
      <AuthProvider>
        <SessionProbe />
      </AuthProvider>,
    );

    expect(await screen.findByText('authenticated')).toBeTruthy();
  });
});
