package com.backmind.note.repository;

import com.backmind.note.entity.Note;
import com.backmind.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NoteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NoteRepository noteRepository;

    @Test
    void savesAndFindsNotesOnlyForTheRequestedUserId() {
        var owner = entityManager.persistAndFlush(
                new User("owner@example.com", "owner-password-hash")
        );
        var otherUser = entityManager.persistAndFlush(
                new User("other@example.com", "other-password-hash")
        );

        var ownersNote = noteRepository.saveAndFlush(
                new Note(owner, "Owner's private note", "Privacy")
        );
        noteRepository.saveAndFlush(
                new Note(otherUser, "Another user's private note", "Privacy")
        );
        entityManager.clear();

        UUID ownerId = owner.getId();
        var result = noteRepository.findAllByUserId(ownerId);

        assertThat(result)
                .extracting(Note::getId)
                .containsExactly(ownersNote.getId());
        assertThat(result)
                .allSatisfy(note -> assertThat(note.getUser().getId()).isEqualTo(ownerId));
    }
}
