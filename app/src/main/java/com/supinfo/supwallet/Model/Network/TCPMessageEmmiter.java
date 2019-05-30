package com.supinfo.supwallet.Model.Network;



import android.util.Log;

import com.supinfo.shared.TCPMessage;
import com.supinfo.supwallet.Model.Network.NetworkUtils.TCPUtils;


import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class TCPMessageEmmiter extends Thread {
    private String hostname;
    private TCPMessage tcpMessage;
    private int port;
    private Socket socket;
    private int timeout;


    public TCPMessageEmmiter(TCPMessage tcpMessage, String hostname, int port, int timeout){
        this.tcpMessage = tcpMessage;
        this.hostname = hostname;
        this.port = port;
        this.timeout = timeout;
    }
    @Override
    public void run() {
        if(TCPUtils.isValidIP(hostname)){
            try {
                Log.i("NETWORK","Trying to send Socket Message on" + hostname);
                socket = new Socket();
                socket.connect(new InetSocketAddress(hostname, port), timeout);
                OutputStream outputStream = socket.getOutputStream();
                // create an object output stream from the output stream so we can send an object through it
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(tcpMessage);
                objectOutputStream.flush();
                objectOutputStream.close();

//            //TESTING WALLET RECEIVING SERVER RESPONSE IN THE SOCKET ITSELF INSTEAD OF A NEW SOCKET RESPONSE
//            InputStream inputStream = socket.getInputStream();
//            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
//            TCPMessage tcpMessage = (TCPMessage) objectInputStream.readObject();
//            cLogger.log(LogLevel.NETWORK, "Successfully sent message and server responded with a: " + tcpMessage.getTcpMessageType());
//
//
//            objectInputStream.close();
//
//            if(tcpMessage.getTcpMessageType() == TCPMessageType.CLOSE_SOCKET) {
//                socket.close();
//                cLogger.log(LogLevel.NETWORK, "TCPEmmiter has sent its message, now closing the socket based on response: " + tcpMessage.getTcpMessageType());
//
//            }

                socket.close();


            } catch (UnknownHostException e){
                Log.e("NETWORK"," You may have input an invalid IP");
            } catch (ConnectException e){
                Log.e("NETWORK",  hostname + " is unreachable!");
                //this is liekly a connection timeout when we try to reach a dead IP, in the case of which we start a checknodes
                //thread in the background to see if all nodes are alive, and take proper action
            } catch (NoRouteToHostException e){
                //do nothing PingPongTask will handle all unreachable hosts
            } catch (SocketTimeoutException e) {
                Log.e("NETWORK",  "Socket timeout after " + timeout);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.e("NETWORK","Invalid IP supplied(self IP or not an IP)!");
        }

    }
}
