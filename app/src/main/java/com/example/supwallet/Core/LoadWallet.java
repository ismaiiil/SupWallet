package com.example.supwallet.Core;


import com.example.supwallet.Core.Utils.CompletionHandler;


public class LoadWallet extends Thread {
    private CompletionHandler<Boolean> success;
    public LoadWallet(CompletionHandler<Boolean> success) {
        this.success = success;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        success.OnFinish(true);
    }


}
