import { fireEvent, render } from '@testing-library/react-native';

import { SignupScreen } from '../src/features/auth/SignupScreen';

describe('SignupScreen', () => {
  it('submits entered account credentials', () => {
    const onSubmit = jest.fn();
    const screen = render(<SignupScreen onSubmit={onSubmit} />);

    fireEvent.changeText(screen.getByLabelText('Email'), 'new-user@example.com');
    fireEvent.changeText(screen.getByLabelText('Password'), 'correct-horse-battery-staple');
    fireEvent.press(screen.getByRole('button', { name: 'Create account' }));

    expect(onSubmit).toHaveBeenCalledWith({
      email: 'new-user@example.com',
      password: 'correct-horse-battery-staple',
    });
  });
});
