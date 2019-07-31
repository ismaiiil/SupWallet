package com.supinfo.supwallet.Model.transactionHelpers;

import com.supinfo.shared.Network.TCPMessage;
import com.supinfo.shared.Network.TCPMessageType;
import com.supinfo.shared.transaction.Transaction;
import com.supinfo.shared.transaction.TransactionInput;
import com.supinfo.shared.transaction.TransactionOutput;
import com.supinfo.supwallet.Model.ENV;
import com.supinfo.supwallet.Model.Network.TCPMessagePoller;
import com.supinfo.supwallet.Model.Utils.AndroidStringUtil;
import com.supinfo.supwallet.Model.Utils.CompletionHandler;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

public class TransactionOperations {
    //fetch UTXOs from local cache to add to the txn
    //add UTXOs to input untill we meet the value the user wants to send to the specific addresses
//
//    public static Transaction getFormattedTransaction(Transaction plainTransaction){
//
//    }

    public static void getAvailableNodeCoins(CompletionHandler<BigDecimal> coins){
        TCPMessage m = new TCPMessage<>(TCPMessageType.WALLET_NODE_BALANCE,null);
        new TCPMessagePoller(m, ENV.connectedIp, ENV.portNumber, ENV.defaultPollTimeout, (response, error) -> {
            if (error == null) {
                coins.onResponse((BigDecimal) response.getData(),null);
            }else{
                coins.onResponse(null,error);
            }

        }).start();
    }
    public static void buyCoins(BigDecimal coinAmount,CompletionHandler<Transaction> pendingTransaction){
        HashMap<PublicKey,BigDecimal> _recipients  = new HashMap<>();
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


    public static ArrayList<TransactionOutput> getMinUTXOForPublicKey(PublicKey publicKey, BigDecimal coins) {
        ArrayList<TransactionOutput> _all = new ArrayList<>();
        BigDecimal _coins = new BigDecimal(0);
        for (TransactionOutput tout : ENV.userUTXOs) {
            if (tout.reciepient.equals(publicKey)) {
                _all.add(tout);
                _coins = _coins.add(tout.value);
                //if a value other than zero is supplied we stop returning txn once we reached the value requested
                if ((coins.compareTo(BigDecimal.ZERO) != 0) && (_coins.compareTo(coins) >= 0)) {
                    return _all;
                }
            }
        }
        return _all;
    }

    public static void sendCoins(BigDecimal coinAmount,PublicKey recipient, CompletionHandler<String> status){
        HashMap<PublicKey,BigDecimal> _recipients  = new HashMap<>();
        _recipients.put(recipient,coinAmount);
        ArrayList<TransactionOutput> minUTXOS = getMinUTXOForPublicKey(recipient,coinAmount);
        ArrayList<TransactionInput> _inputs = new ArrayList<>();
        for (TransactionOutput tout : minUTXOS) {
            _inputs.add(new TransactionInput(tout.id, tout));
        }
        Transaction transaction = new Transaction(ENV.wallet.getPublic(),_recipients,new ArrayList<>());
        transaction.inputs = _inputs;
        //put change back to us
        transaction.recipients.put(ENV.wallet.getPublic(),transaction.getInputsValue().subtract(coinAmount));
        transaction.transactionId = AndroidStringUtil.calulateHashTransaction(transaction);
        //As for POC ill demonstrate to the recipient and a change back to the user base
        ArrayList<TransactionOutput> _txnOutputs = new ArrayList<>();
        TransactionOutput _tout = new TransactionOutput(recipient, coinAmount, transaction.transactionId, null);
        _tout.id = AndroidStringUtil.generateTransactionOutputThisId(_tout);
        TransactionOutput _change = new TransactionOutput(ENV.wallet.getPublic(), transaction.getInputsValue().subtract(coinAmount), transaction.transactionId, null);
        _change.id = AndroidStringUtil.generateTransactionOutputThisId(_change);
        _txnOutputs.add(_tout);
        _txnOutputs.add(_change);
        transaction.outputs = _txnOutputs;
        transaction.signature = AndroidStringUtil.generateSignature(ENV.wallet.getPrivate(), transaction);
        TCPMessage<Transaction> transactionTCPMessage=  new TCPMessage<>(TCPMessageType.PROPAGATE_NEW_TXN_MEMPOOL,0, transaction);
        new TCPMessagePoller(transactionTCPMessage, ENV.connectedIp, ENV.portNumber, ENV.defaultPollTimeout, (response, error) -> {
            if (error == null) {
                if(response.getTcpMessageType() == TCPMessageType.NEW_TXN_MEMPOOL){
                    status.onResponse("Success sending transaction to mempool!",null);
                }else{
                    status.onResponse("An Error occurred while checking your transaction!",null);
                }

            }else{
                status.onResponse(null,error);
            }
        }).start();
    }
}
