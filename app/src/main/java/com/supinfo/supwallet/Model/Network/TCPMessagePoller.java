package com.supinfo.supwallet.Model.Network;



import android.util.Log;

import com.supinfo.shared.Network.TCPMessage;
import com.supinfo.shared.Utils.StringUtil;
import com.supinfo.supwallet.Model.Utils.CompletionHandler;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class TCPMessagePoller extends Thread {
    private String hostname;
    private TCPMessage tcpMessage;
    private int port;
    private Socket socket;
    private int timeout;
    private CompletionHandler<TCPMessage> response;


    public TCPMessagePoller(TCPMessage tcpMessage, String hostname, int port, int timeout, CompletionHandler<TCPMessage> response){
        this.tcpMessage = tcpMessage;
        this.hostname = hostname;
        this.port = port;
        this.timeout = timeout;
        this.response = response;
    }
    @Override
    public void run() {
        if(StringUtil.isValidIP(hostname)){
            try {
                Log.i("NETWORK","Trying to send Socket Message on" + hostname);
                socket = new Socket();
                socket.connect(new InetSocketAddress(hostname, port), timeout);
                OutputStream outputStream = socket.getOutputStream();
                // create an object output stream from the output stream so we can send an object through it
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(tcpMessage);


                //TESTING WALLET RECEIVING SERVER RESPONSE IN THE SOCKET ITSELF INSTEAD OF A NEW SOCKET RESPONSE
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                TCPMessage tcpMessage = (TCPMessage) objectInputStream.readObject();

                objectOutputStream.flush();
                objectOutputStream.close();
                objectInputStream.close();

                socket.close();
                Log.i("NETWORK", "Successfully sent message and server responded with a: " + tcpMessage.getTcpMessageType()+ tcpMessage.getData());
                response.onResponse(tcpMessage,null);


            } catch (UnknownHostException e){
                Log.e("NETWORK"," You may have input an invalid IP");
                response.onResponse(null,e);
            } catch (ConnectException e){
                Log.e("NETWORK",  hostname + " is unreachable!");
                //this is liekly a connection timeout when we try to reach a dead IP, in the case of which we start a checknodes
                //thread in the background to see if all nodes are alive, and take proper action
                response.onResponse(null,e);
            } catch (NoRouteToHostException e){
                //do nothing PingPongTask will handle all unreachable hosts
                response.onResponse(null,e);
            } catch (SocketTimeoutException e) {
                Log.e("NETWORK",  "Socket timeout after " + timeout);
                response.onResponse(null,e);
            } catch (IOException e) {
                e.printStackTrace();
                response.onResponse(null,e);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                response.onResponse(null,e);
            }
        }else{
            Log.e("NETWORK","Invalid IP supplied(self IP or not an IP)!");
            response.onResponse(null,new Exception());
        }

    }
}
