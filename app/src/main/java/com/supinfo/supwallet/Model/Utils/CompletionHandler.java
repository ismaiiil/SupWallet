package com.supinfo.supwallet.Model.Utils;

public interface CompletionHandler<T> {
    void OnFinish(T response);
}
