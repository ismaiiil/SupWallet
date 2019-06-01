package com.supinfo.supwallet.Model.Network;

import com.supinfo.supwallet.Model.Utils.CompletionHandler;

public class TimerThread extends Thread{
    CompletionHandler<Boolean> isFinished;
    long time;

    public TimerThread(long time,CompletionHandler<Boolean> isFinished){
        this.time = time;
        this.isFinished = isFinished;
    }

    @Override
    public void run() {
        try {
            sleep(time);
            isFinished.onResponse(true,null);
        } catch (InterruptedException e) {
            e.printStackTrace();
            isFinished.onResponse(false,e);
        }
    }
}
