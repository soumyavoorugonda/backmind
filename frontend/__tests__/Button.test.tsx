import { fireEvent, render } from '@testing-library/react-native';

import { Button } from '../src/components/ui/Button';

describe('Button', () => {
  it('renders its label and delegates presses', () => {
    const onPress = jest.fn();
    const screen = render(<Button label="Continue" onPress={onPress} />);

    fireEvent.press(screen.getByRole('button', { name: 'Continue' }));

    expect(onPress).toHaveBeenCalledTimes(1);
  });
});
