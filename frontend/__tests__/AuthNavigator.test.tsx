import { fireEvent, render } from '@testing-library/react-native';

import { AuthNavigator } from '../src/navigation/AuthNavigator';

describe('AuthNavigator', () => {
  it('moves between login and signup screens', () => {
    const screen = render(<AuthNavigator />);

    expect(screen.getByRole('header', { name: 'Welcome back' })).toBeTruthy();

    fireEvent.press(screen.getByRole('button', { name: 'Sign up' }));
    expect(screen.getByRole('header', { name: 'Create your account' })).toBeTruthy();

    fireEvent.press(screen.getByRole('button', { name: 'Back to login' }));
    expect(screen.getByRole('header', { name: 'Welcome back' })).toBeTruthy();
  });
});
