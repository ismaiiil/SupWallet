package com.supinfo.supwallet.Model.Network;

import com.supinfo.shared.Network.TCPMessage;
import com.supinfo.shared.Network.TCPMessageType;
import com.supinfo.shared.transaction.TransactionOutput;
import com.supinfo.supwallet.Model.ENV;
import com.supinfo.supwallet.Model.Utils.CompletionHandler;
import com.supinfo.supwallet.Presenter.Adapters.IpRowModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.TimeZone;

public class TCPMessageOperations {
    public static void getLatency(String ipAddress, CompletionHandler<Long> time){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long currentTime = cal.getTimeInMillis();

        TCPMessage m = new TCPMessage<>(TCPMessageType.WALLET_PING,null);
        new TCPMessagePoller(m, ipAddress, ENV.portNumber, ENV.defaultPollTimeout, (response,error) -> {
            if (error == null) {
                Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                long responseTime = cal1.getTimeInMillis();
                time.onResponse(responseTime - currentTime,null);
            }else{
                time.onResponse(null,error);
            }

        }).start();
    }

    public static void getIPList(String ipAddress, CompletionHandler<HashSet<String>> list){
        TCPMessage m = new TCPMessage<>(TCPMessageType.WALLET_LIST_NODES,null);
        new TCPMessagePoller(m, ipAddress, ENV.portNumber, ENV.defaultPollTimeout, (response,error) -> {
            if (error == null) {
                HashSet<String> ips = (HashSet<String>) response.getData();
                list.onResponse(ips,null);
            }else{
                list.onResponse(null,error);
            }


        }).start();
    }

    public static void getIPLatencyList(String ipAddress, CompletionHandler<ArrayList<IpRowModel>> list){
        TCPMessageOperations.getIPList(ipAddress , (response,error) -> {
            if (error == null) {
                ArrayList<IpRowModel> latencies = new ArrayList<>();

                for (String ip : response) {
                    getLatency(ip, (response1, error1) -> {
                        if(error1 == null){
                            latencies.add(new IpRowModel(ip,response1));
                        }else{
                            latencies.add(new IpRowModel(ip,-99));
                        }

                    });
                }

                while(latencies.size() < response.size()){
                }

                list.onResponse(latencies,null);
            }else{
                list.onResponse(null,error);
            }

        });
    }

    public static void getUTXOS( CompletionHandler<ArrayList<TransactionOutput>> utxoList){
        TCPMessage m = new TCPMessage<>(TCPMessageType.WALLET_FETCH_UTXOS,ENV.wallet.getPublic());
        new TCPMessagePoller(m, ENV.connectedIp, ENV.portNumber, ENV.defaultPollTimeout, (response,error) -> {
            if (error == null) {
                ArrayList<TransactionOutput> utxos = (ArrayList<TransactionOutput>) response.getData();
                utxoList.onResponse(utxos,null);
            }else{
                utxoList.onResponse(null,error);
            }


        }).start();

    }
}
