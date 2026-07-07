import { fireEvent, render, waitFor } from '@testing-library/react-native';

jest.mock('../src/features/notes/noteApi', () => ({
  noteApi: { create: jest.fn(), findAll: jest.fn(), review: jest.fn(), today: jest.fn() },
}));

import { noteApi } from '../src/features/notes/noteApi';
import { AppNavigator } from '../src/navigation/AppNavigator';

const note = {
  id: 'note-1',
  content: 'Diversification reduces risk.',
  category: 'Investing',
  createdAt: '2026-07-06T12:00:00Z',
  updatedAt: '2026-07-06T12:00:00Z',
  lastSeenAt: null,
  nextReviewAt: null,
  currentIntervalDays: 1,
  status: 'ACTIVE' as const,
  usefulnessScore: 0,
  beliefStatus: 'STILL_BELIEVE' as const,
};

describe('app API integration', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.mocked(noteApi.today).mockResolvedValue([]);
  });

  it('creates a note and reports success', async () => {
    jest.mocked(noteApi.create).mockResolvedValue(note);
    const screen = render(<AppNavigator onLogout={jest.fn()} token="jwt" />);

    fireEvent.press(screen.getByRole('button', { name: 'New note' }));
    fireEvent.changeText(screen.getByLabelText('Note content'), note.content);
    fireEvent.changeText(screen.getByLabelText('Category'), note.category);
    fireEvent.press(screen.getByRole('button', { name: 'Save note' }));

    await waitFor(() =>
      expect(noteApi.create).toHaveBeenCalledWith('jwt', {
        content: note.content,
        category: note.category,
      }),
    );
    expect(await screen.findByText('Note saved')).toBeTruthy();
  });

  it('loads the today feed and submits a review', async () => {
    jest.mocked(noteApi.today).mockResolvedValue([note]);
    jest.mocked(noteApi.review).mockResolvedValue(note);
    const screen = render(<AppNavigator onLogout={jest.fn()} token="jwt" />);

    expect(await screen.findByText(note.content)).toBeTruthy();

    fireEvent.press(screen.getByRole('button', { name: 'Useful' }));
    await waitFor(() => expect(noteApi.review).toHaveBeenCalledWith('jwt', 'note-1', 'USEFUL'));
  });

  it('shows note request failures', async () => {
    jest.mocked(noteApi.today).mockRejectedValue(new Error('Unable to load notes'));
    const screen = render(<AppNavigator onLogout={jest.fn()} token="jwt" />);

    expect(await screen.findByText('Unable to load notes')).toBeTruthy();
  });

  it('loads all notes for the My notes screen', async () => {
    jest.mocked(noteApi.findAll).mockResolvedValue([note]);
    const screen = render(<AppNavigator onLogout={jest.fn()} token="jwt" />);

    fireEvent.press(screen.getByRole('button', { name: 'My notes' }));

    expect(await screen.findByText(note.content)).toBeTruthy();
    expect(noteApi.findAll).toHaveBeenCalledWith('jwt');
  });
});
