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
    public static ArrayList<TransactionOutput> userUTXOs  = new ArrayList<>(); //these are incoming txns that havent been used a inputs yet
    public static ArrayList<Transaction> pendingIncomingTransactions = new ArrayList<>();
}
