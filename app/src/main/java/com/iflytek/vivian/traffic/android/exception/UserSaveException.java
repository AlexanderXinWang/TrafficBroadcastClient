package com.iflytek.vivian.traffic.android.exception;

public class UserSaveException extends RuntimeException {
    public UserSaveException() {
    }

    public UserSaveException(String message) {
        super(message);
    }

    public UserSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserSaveException(Throwable cause) {
        super(cause);
    }

}
