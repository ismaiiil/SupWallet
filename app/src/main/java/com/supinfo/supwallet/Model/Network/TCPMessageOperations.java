package com.supinfo.supwallet.Model.Network;

import com.supinfo.shared.Network.TCPMessage;
import com.supinfo.shared.Network.TCPMessageType;
import com.supinfo.supwallet.Model.ENV;
import com.supinfo.supwallet.Model.Utils.CompletionHandler;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TimeZone;

public class TCPMessageOperations {
    public static void getLatency(String ipAddress, CompletionHandler<Long> time){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long currentTime = cal.getTimeInMillis();

        TCPMessage m = new TCPMessage<>(TCPMessageType.WALLET_PING,null);
        new TCPMessagePoller(m, ipAddress, ENV.portNumber, ENV.defaultPollTimeout, response -> {
            Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            long responseTime = cal1.getTimeInMillis();
            time.OnFinish(responseTime - currentTime);
        }).start();
    }

    public static void getIPLatencyList(String ipAddress, CompletionHandler<HashSet<String>> list){
        TCPMessage m = new TCPMessage<>(TCPMessageType.WALLET_LIST_NODES,null);
        new TCPMessagePoller(m, ipAddress, ENV.portNumber, ENV.defaultPollTimeout, response -> {
            HashSet<String> ips = (HashSet<String>) response.getData();
            list.OnFinish(ips);
        }).start();
    }
}
