package com.mthree.backend.utils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseType<T> {
    String message;
    T data;
    int statusCode;
    boolean success;

    public ResponseType(String message, T data, int statusCode) {
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
        this.success = statusCode >= 200 && statusCode < 400;
    }
}
