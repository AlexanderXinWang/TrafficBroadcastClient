package com.iflytek.vivian.traffic.android.exception;

public class ApiInvokeException extends RuntimeException {
    public ApiInvokeException() {
    }

    public ApiInvokeException(String message) {
        super(message);
    }

    public ApiInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiInvokeException(Throwable cause) {
        super(cause);
    }

}
