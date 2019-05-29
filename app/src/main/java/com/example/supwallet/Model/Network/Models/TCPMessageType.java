package com.example.supwallet.Model.Network.Models;

public enum TCPMessageType {
    VERIFY,
    REQUEST_CONNECTION,
    CONFIRM_CONNECTION,
    MESSENGER_REQ,
    MESSENGER_ACK,
    WAIT_FOR_LOOKUP,
    PING,
    PONG,
    UPDATE_SENDER_IP,
    WALLET_CONNECT
}
