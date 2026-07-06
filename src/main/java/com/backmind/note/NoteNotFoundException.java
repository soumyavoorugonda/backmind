package com.backmind.note;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException() {
        super("Note not found");
    }
}
