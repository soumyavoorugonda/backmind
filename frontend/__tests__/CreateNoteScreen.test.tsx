import { fireEvent, render } from '@testing-library/react-native';

import { CreateNoteScreen } from '../src/features/notes/CreateNoteScreen';
import { colors } from '../src/theme/tokens';

describe('CreateNoteScreen', () => {
  it('submits note content and optional category', () => {
    const onSubmit = jest.fn();
    const screen = render(<CreateNoteScreen onSubmit={onSubmit} />);

    fireEvent.changeText(screen.getByLabelText('Note content'), 'Diversification reduces risk.');
    fireEvent.changeText(screen.getByLabelText('Category'), 'Investing');
    fireEvent.press(screen.getByRole('button', { name: 'Save note' }));

    expect(onSubmit).toHaveBeenCalledWith({
      content: 'Diversification reduces risk.',
      category: 'Investing',
    });
  });

  it('uses subdued placeholder text for note fields', () => {
    const screen = render(<CreateNoteScreen onSubmit={jest.fn()} />);

    expect(colors.placeholder).toBe('#91A19A');
    expect(screen.getByLabelText('Note content').props.placeholderTextColor).toBe(
      '#91A19A',
    );
    expect(screen.getByLabelText('Category').props.placeholderTextColor).toBe(
      '#91A19A',
    );
  });
});
