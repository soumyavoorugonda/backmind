import { render } from '@testing-library/react-native';

jest.mock('../src/features/auth/AuthContext', () => ({
  useAuth: jest.fn(() => ({
    status: 'unauthenticated',
    token: null,
    signIn: jest.fn(),
    signOut: jest.fn(),
  })),
}));

import { SessionNavigator } from '../src/navigation/SessionNavigator';

describe('SessionNavigator', () => {
  it('shows the login flow when no session exists', async () => {
    const screen = render(<SessionNavigator />);

    expect(await screen.findByRole('header', { name: 'Welcome back' })).toBeTruthy();
    expect(screen.queryByRole('header', { name: 'BackMind' })).toBeNull();
  });
});
