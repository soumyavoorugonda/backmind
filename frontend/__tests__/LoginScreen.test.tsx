import { fireEvent, render } from '@testing-library/react-native';

import { LoginScreen } from '../src/features/auth/LoginScreen';

describe('LoginScreen', () => {
  it('submits entered credentials', () => {
    const onSubmit = jest.fn();
    const screen = render(<LoginScreen onSubmit={onSubmit} />);

    fireEvent.changeText(screen.getByLabelText('Email'), 'learner@example.com');
    fireEvent.changeText(screen.getByLabelText('Password'), 'correct-horse-battery-staple');
    fireEvent.press(screen.getByRole('button', { name: 'Log in' }));

    expect(onSubmit).toHaveBeenCalledWith({
      email: 'learner@example.com',
      password: 'correct-horse-battery-staple',
    });
  });

  it('toggles password visibility with the eye button', () => {
    const screen = render(<LoginScreen onSubmit={jest.fn()} />);
    const passwordInput = screen.getByLabelText('Password');

    expect(passwordInput.props.secureTextEntry).toBe(true);

    fireEvent.press(screen.getByRole('button', { name: 'Show password' }));
    expect(screen.getByLabelText('Password').props.secureTextEntry).toBe(false);

    fireEvent.press(screen.getByRole('button', { name: 'Hide password' }));
    expect(screen.getByLabelText('Password').props.secureTextEntry).toBe(true);
  });
});
