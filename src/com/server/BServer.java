package com.server;
import web_tools.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public abstract class BServer implements Runnable{
    private int edgePort;
    protected ServerSocket serverSocket;
    public BServer(int edgePort) {
        this.edgePort = edgePort;
        try {
            this.serverSocket = new ServerSocket(edgePort);
            System.out.println("init server over. port is " + edgePort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public void startServer(){
        while(this.serverSocket != null)
            start();
    }
    protected abstract void start();

//    void open(){
//        while(serverSocket != null){
//            try {
//                BChannel channel = new BChannel(serverSocket.accept());
//                channalMap.put(channel.toString(), channel);
//                System.out.println("new channel:"+channel);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
    @Override
    public void run(){
        //todo

    }
}
