package com.iflytek.vivian.traffic.android.event;

/**
 * 基础网络请求结果
 * @param <T>
 */
public class BaseNetworkEvent<T> {
    private boolean success;
    private String errorMessage;
    private Exception e;
    private T data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BaseNetworkEvent(boolean success, String errorMessage, Exception e, T data) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.e = e;
        this.data = data;
    }
}
