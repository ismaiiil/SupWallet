package com.supinfo.supwallet.Model;

import com.supinfo.shared.transaction.Transaction;
import com.supinfo.shared.transaction.TransactionOutput;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;

public class ENV {
    final public static int portNumber = 8888;
    final public static int defaultPollTimeout = 10000;
    final public static String GROUP_PREF = "SUP_WALLET";
    final public static String PREF_IS_FIRST_TIME = "PREF_IS_FIRST_TIME";
    public static final String PREF_CONNECTED_IP = "PREF_CONNECTED_IP";
    public static final String PREF_WALLET = "PREF_WALLET";
    public static KeyPair wallet;
    public static String defaultBootNode = "192.168.100.6"; //18.139.62.95 192.168.100.2
    public static String connectedIp = "";
    public static HashMap<String, TransactionOutput> userUTXOs  = new HashMap<>(); //these are incoming txns that havent been used a inputs yet
    public static HashMap<String, TransactionOutput> lockedUTXOs  = new HashMap<>(); //these txouts that we might  have used for a txn and for performance reasons we dnt want the user to use it again
    //to be clear it is possible for anyone to double spend a UTXO, however once the miner sees there is a double spend the second one will be discarded,
    //we are just using this locked UTXOs for better User X
    //whenever he uses a UTXO part of his balance (UTXO value) gets blocked and placed in the locked UTXOS until the transaction is mined
    //whenever his txns is mined his data must be updated, the wallet will keep some sort of reference to which block it was last synced,
    //and will try to sync from the new part of the blockchain instead of loading the whole blockchain, we can just send the pending transactions periodically
    //to be scanned on the blockchain so that we can update the cache on the wallet
    public static ArrayList<Transaction> pendingIncomingTransactions = new ArrayList<>();
    public static ArrayList<Transaction> pendingOutgoingTransactions = new ArrayList<>();
    public static ArrayList<Transaction> confirmedIncomingTransactions = new ArrayList<>();
    public static ArrayList<Transaction> confirmedOutgoingransactions = new ArrayList<>();
}
