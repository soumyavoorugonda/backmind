import { fireEvent, render, waitFor } from '@testing-library/react-native';

const mockSignIn = jest.fn();

jest.mock('../src/features/auth/AuthContext', () => ({
  useAuth: () => ({ status: 'unauthenticated', token: null, signIn: mockSignIn, signOut: jest.fn() }),
}));
jest.mock('../src/features/auth/authApi', () => ({
  authApi: { login: jest.fn(), logout: jest.fn(), signup: jest.fn() },
}));

import { authApi } from '../src/features/auth/authApi';
import { SessionNavigator } from '../src/navigation/SessionNavigator';

describe('authenticated session integration', () => {
  beforeEach(() => jest.clearAllMocks());

  it('logs in through the API and stores the returned token', async () => {
    jest.mocked(authApi.login).mockResolvedValue({ token: 'server-jwt' });
    const screen = render(<SessionNavigator />);

    fireEvent.changeText(screen.getByLabelText('Email'), 'demo@backmind.local');
    fireEvent.changeText(screen.getByLabelText('Password'), 'Secret@password');
    fireEvent.press(screen.getByRole('button', { name: 'Log in' }));

    await waitFor(() =>
      expect(authApi.login).toHaveBeenCalledWith({
        email: 'demo@backmind.local',
        password: 'Secret@password',
      }),
    );
    expect(mockSignIn).toHaveBeenCalledWith('server-jwt');
  });

  it('shows backend authentication errors', async () => {
    jest.mocked(authApi.login).mockRejectedValue(new Error('Invalid email or password'));
    const screen = render(<SessionNavigator />);

    fireEvent.press(screen.getByRole('button', { name: 'Log in' }));

    expect(await screen.findByText('Invalid email or password')).toBeTruthy();
  });

  it('creates an account and starts a session', async () => {
    jest.mocked(authApi.signup).mockResolvedValue({ id: 'user-1', email: 'new@example.com' });
    jest.mocked(authApi.login).mockResolvedValue({ token: 'new-user-jwt' });
    const screen = render(<SessionNavigator />);

    fireEvent.press(screen.getByRole('button', { name: 'Sign up' }));
    fireEvent.changeText(screen.getByLabelText('Email'), 'new@example.com');
    fireEvent.changeText(screen.getByLabelText('Password'), 'Secret@password');
    fireEvent.press(screen.getByRole('button', { name: 'Create account' }));

    await waitFor(() =>
      expect(authApi.signup).toHaveBeenCalledWith({
        email: 'new@example.com',
        password: 'Secret@password',
      }),
    );
    expect(authApi.login).toHaveBeenCalledWith({
      email: 'new@example.com',
      password: 'Secret@password',
    });
    expect(mockSignIn).toHaveBeenCalledWith('new-user-jwt');
  });
});
