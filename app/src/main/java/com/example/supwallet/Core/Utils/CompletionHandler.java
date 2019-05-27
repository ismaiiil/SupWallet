package com.example.supwallet.Core.Utils;

public interface CompletionHandler<T> {
    void OnFinish(T response);
}
