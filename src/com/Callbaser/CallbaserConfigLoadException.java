package com.Callbaser;

public class CallbaserConfigLoadException extends Exception {

    public Exception cause;

    public CallbaserConfigLoadException(Exception e) {
        this.cause = e;
    }
}
