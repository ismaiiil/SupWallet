package com.supinfo.supwallet.Model.Utils;

public interface CompletionHandler<T> {
    void onResponse(T response, Exception error);
}
