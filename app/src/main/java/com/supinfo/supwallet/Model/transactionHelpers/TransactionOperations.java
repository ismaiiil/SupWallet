package com.supinfo.supwallet.Model.transactionHelpers;

import com.supinfo.shared.Network.TCPMessage;
import com.supinfo.shared.Network.TCPMessageType;
import com.supinfo.shared.transaction.Transaction;
import com.supinfo.supwallet.Model.ENV;
import com.supinfo.supwallet.Model.Network.TCPMessagePoller;
import com.supinfo.supwallet.Model.Utils.CompletionHandler;

import java.security.PublicKey;
import java.util.HashMap;

public class TransactionOperations {
    //fetch UTXOs from local cache to add to the txn
    //add UTXOs to input untill we meet the value the user wants to send to the specific addresses
//
//    public static Transaction getFormattedTransaction(Transaction plainTransaction){
//
//    }

    public static void getAvailableNodeCoins(CompletionHandler<Float> coins){
        TCPMessage m = new TCPMessage<>(TCPMessageType.WALLET_NODE_BALANCE,null);
        new TCPMessagePoller(m, ENV.connectedIp, ENV.portNumber, ENV.defaultPollTimeout, (response, error) -> {
            if (error == null) {
                coins.onResponse((Float) response.getData(),null);
            }else{
                coins.onResponse(null,error);
            }

        }).start();
    }
    public static void buyCoins(float coinAmount,CompletionHandler<Transaction> pendingTransaction){
        HashMap<PublicKey,Float> _recipients  = new HashMap<>();
        _recipients.put(ENV.wallet.getPublic(),coinAmount);
        Transaction transaction = new Transaction(null,_recipients,null);
        TCPMessage m = new TCPMessage<>(TCPMessageType.WALLET_BUY_COINS,transaction);
        new TCPMessagePoller(m, ENV.connectedIp, ENV.portNumber, ENV.defaultPollTimeout, (response, error) -> {
            if (error == null) {
                if(response.getTcpMessageType() == TCPMessageType.WALLET_SUCCESS_BUY){
                    pendingTransaction.onResponse((Transaction) response.getData(),null);
                }else{
                    pendingTransaction.onResponse(null,null);
                }

            }else{
                pendingTransaction.onResponse(null,error);
            }

        }).start();
    }
}
