package com.supinfo.supwallet.Model.FileManagers;


import com.supinfo.supwallet.Model.Utils.CompletionHandler;


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