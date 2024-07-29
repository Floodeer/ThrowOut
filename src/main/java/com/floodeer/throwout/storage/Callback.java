package com.floodeer.throwout.storage;

public interface Callback<T> {

    void onCall(T result);
}