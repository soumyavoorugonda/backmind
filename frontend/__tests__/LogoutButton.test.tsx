import { fireEvent, render } from '@testing-library/react-native';

const mockSignOut = jest.fn();

jest.mock('../src/features/auth/AuthContext', () => ({
  useAuth: jest.fn(() => ({ signOut: mockSignOut })),
}));

import { LogoutButton } from '../src/features/auth/LogoutButton';

describe('LogoutButton', () => {
  it('clears the authenticated session', () => {
    const screen = render(<LogoutButton />);

    fireEvent.press(screen.getByRole('button', { name: 'Log out' }));

    expect(mockSignOut).toHaveBeenCalledTimes(1);
  });
});
