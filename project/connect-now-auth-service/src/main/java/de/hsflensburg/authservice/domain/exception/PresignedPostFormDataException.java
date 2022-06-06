package de.hsflensburg.authservice.domain.exception;

public class PresignedPostFormDataException extends Exception {
    public PresignedPostFormDataException() {}

    public PresignedPostFormDataException(String msg) {
        super(msg);
    }
}
