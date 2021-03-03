/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.iflytek.vivian.traffic.android.dto;

/**
 * 通用结果包装类
 * @param <T>
 */
public class Result<T> {
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 错误代码，非0就为有错误
     */
    private int errorCode ;
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 包装的数据对象
     * 使用模板类包装比用Object更好
     */
    private T data;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 根据错误代码值，和错误提示信息生成返回结果
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail(int code, String message) {
        Result<T> result = new Result<T>();
        result.errorCode = code;
        result.success = false;
        result.errorMessage = message;
        return result;
    }

    /**
     * 只有错误返回结果，错误代码统一为Fail
     * @param message
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail(String message) {
        return fail(-1,message);
    }

    /**
     * 成功并返回数据对象
     * @param value
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T value) {
        Result<T> result = new Result<T>();
        result.setData(value);
        result.success = true;
        return result;
    }
}
